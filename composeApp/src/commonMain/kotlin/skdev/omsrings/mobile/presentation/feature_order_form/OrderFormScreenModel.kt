package skdev.omsrings.mobile.presentation.feature_order_form

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShoppingCart
import cafe.adriel.voyager.core.model.screenModelScope
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.cant_be_blank
import omsringsmobile.composeapp.generated.resources.order_created
import omsringsmobile.composeapp.generated.resources.order_created_message
import omsringsmobile.composeapp.generated.resources.order_updated
import omsringsmobile.composeapp.generated.resources.order_updated_message
import omsringsmobile.composeapp.generated.resources.time_cant_be_empty
import org.jetbrains.compose.resources.StringResource
import skdev.omsrings.mobile.domain.model.DeliveryMethod
import skdev.omsrings.mobile.domain.model.InventoryItem
import skdev.omsrings.mobile.domain.model.Order
import skdev.omsrings.mobile.domain.model.OrderHistoryEvent
import skdev.omsrings.mobile.domain.model.OrderHistoryEventType
import skdev.omsrings.mobile.domain.model.OrderItem
import skdev.omsrings.mobile.domain.model.OrderStatus
import skdev.omsrings.mobile.domain.usecase.feature_order.CreateOrderUseCase
import skdev.omsrings.mobile.domain.usecase.feature_order.GetFoldersAndItemsInventory
import skdev.omsrings.mobile.domain.usecase.feature_order.GetInventoryItemsByIdsUseCase
import skdev.omsrings.mobile.domain.usecase.feature_order.GetOrderByIdUseCase
import skdev.omsrings.mobile.domain.usecase.feature_order.UpdateOrderUseCase
import skdev.omsrings.mobile.presentation.base.BaseScreenModel
import skdev.omsrings.mobile.presentation.feature_order_form.components.ProductSelectionEvent
import skdev.omsrings.mobile.presentation.feature_order_form.components.ProductSelectionState
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.flowBlock
import skdev.omsrings.mobile.utils.fields.validators.ValidationResult
import skdev.omsrings.mobile.utils.fields.validators.notBlank
import skdev.omsrings.mobile.utils.fields.validators.notEmpty
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.omsrings.mobile.utils.notification.NotificationModel
import skdev.omsrings.mobile.utils.notification.ToastType
import skdev.omsrings.mobile.utils.result.ifSuccess
import skdev.omsrings.mobile.utils.uuid.randomUUID

private const val MILLIS_IN_SECOND = 1000L

class OrderFormScreenModel(
    private val notificationManager: NotificationManager,
    private val createOrderUseCase: CreateOrderUseCase,
    private val updateOrderUseCase: UpdateOrderUseCase,
    private val getFoldersAndItemsInventory: GetFoldersAndItemsInventory,
    private val getOrderByIdUseCase: GetOrderByIdUseCase,
    private val getInventoryItemsByIdsUseCase: GetInventoryItemsByIdsUseCase,
    private val selectedDate: Timestamp,
    private val orderId: String? = null
) : BaseScreenModel<OrderFormContract.Event, OrderFormContract.Effect>(
    notificationManager
) {

    private var existingOrder: Order? = null

    private val _state = MutableStateFlow(
        OrderFormContract.State(
            // Common
            isLoading = false,
            isEditMode = orderId != null,
            orderId = orderId,

            // Form
            contactPhoneField = createPhoneField(),
            deliveryDate = "15.07.2024",
            deliveryMethod = DeliveryMethod.PICKUP,
            deliveryAddressField = createAddressField(),
            deliveryTimeField = createTimeField(),
            deliveryCommentField = createCommentField(),

            // Inventory selection
            productSelectionState = ProductSelectionState(
                folders = emptyList(),
                selectedFolderId = null,
                selectedItemId = null,
                selectedItems = emptyMap()
            )
        )
    )
    val state = _state.asStateFlow()

    private fun createPhoneField() = FormField<String, StringResource>(
        scope = screenModelScope,
        initialValue = "",
        validation = flowBlock {
            ValidationResult.of(it) {
                notBlank(Res.string.cant_be_blank)
            }
        }
    )


    private fun createAddressField() = FormField<String, StringResource>(
        scope = screenModelScope,
        initialValue = "",
        validation = flowBlock {
            ValidationResult.of(it) {
                notBlank(Res.string.cant_be_blank)
            }
        }
    )

    private fun createTimeField() = FormField<String, StringResource>(
        scope = screenModelScope,
        initialValue = "",
        validation = flowBlock {
            ValidationResult.of(it) {
                notEmpty(Res.string.time_cant_be_empty)
            }
        }
    )

    private fun createCommentField() = FormField<String, StringResource>(
        scope = screenModelScope,
        initialValue = "",
        validation = flowBlock { null }
    )

    init {
        loadInventory()
        orderId?.let { loadExistingOrder(it) }
    }

    override fun onEvent(event: OrderFormContract.Event) {
        when (event) {
            is OrderFormContract.Event.OnDeliveryMethodChanged -> updateDeliveryMethod(event.method)
            is OrderFormContract.Event.OnProductSelectionEvent -> handleProductSelectionEvent(event.event)
            OrderFormContract.Event.OnSubmitClicked -> submitOrder()
            is OrderFormContract.Event.OnBackClicked -> TODO()
            is OrderFormContract.Event.LoadExistingOrder -> loadExistingOrder(event.orderId)

        }
    }

    private fun handleProductSelectionEvent(event: ProductSelectionEvent) {
        when (event) {
            is ProductSelectionEvent.OnFolderSelected -> updateSelectedFolder(event.folderId)
            is ProductSelectionEvent.OnItemQuantityChanged -> updateSelectedItem(event.item, event.quantity)
        }
    }

    private fun updateSelectedFolder(folderId: String?) {
        _state.update { it.copy(productSelectionState = it.productSelectionState.copy(selectedFolderId = folderId)) }
    }

    private fun updateSelectedItem(item: InventoryItem, quantity: Int) {
        val updatedItems = _state.value.productSelectionState.selectedItems.toMutableMap()
        if (quantity > 0) {
            updatedItems[item] = quantity
        } else {
            updatedItems.remove(item)
        }
        _state.update { it.copy(productSelectionState = it.productSelectionState.copy(selectedItems = updatedItems)) }
    }

    private fun updateDeliveryMethod(method: DeliveryMethod) {
        _state.value = _state.value.copy(deliveryMethod = method)
    }

    private fun loadInventory() {
        screenModelScope.launch {
            getFoldersAndItemsInventory().ifSuccess { foldersAndItemsFlow ->
                foldersAndItemsFlow.data.collect { folders ->
                    _state.update { it.copy(productSelectionState = it.productSelectionState.copy(folders = folders)) }
                }
            }
        }
    }

    private suspend fun validateForm(): Boolean {
        val phoneValid = _state.value.contactPhoneField.validate()
        val timeValid = _state.value.deliveryTimeField.validate()
        val commentValid = _state.value.deliveryCommentField.validate()
        val addressValid = if (state.value.deliveryMethod == DeliveryMethod.DELIVERY) {
            _state.value.deliveryAddressField.validate()
        } else true
        val productSelectionValid = _state.value.productSelectionState.selectedItems.isNotEmpty()

        return phoneValid && timeValid && addressValid && commentValid && productSelectionValid
    }


    private fun createOrderFromState(existingOrder: Order?): Order {
        val currentTime = Timestamp.now()
        val newHistoryEvent = OrderHistoryEvent(
            time = currentTime,
            type = if (existingOrder != null) OrderHistoryEventType.CHANGED else OrderHistoryEventType.CREATED,
            user = getCurrentUserId()
        )


        return Order(
            id = existingOrder?.id ?: randomUUID(),
            date = selectedDate,
            address = _state.value.deliveryAddressField.data.value,
            comment = _state.value.deliveryCommentField.data.value,
            contactPhone = _state.value.contactPhoneField.data.value,
            isDelivery = _state.value.deliveryMethod == DeliveryMethod.DELIVERY,
            pickupTime = timestampFromDeliveryTime(_state.value.deliveryTimeField.data.value),
            status = existingOrder?.status ?: OrderStatus.CREATED,
            history = (existingOrder?.history ?: emptyList()) + newHistoryEvent,
            items = _state.value.productSelectionState.selectedItems.map { (item, quantity) ->
                OrderItem(inventoryId = item.id, quantity = quantity)
            }
        )
    }

    private fun getCurrentUserId(): String {
        // TODO: Implement actual user UUID retrieval
        return "current_user_id"
    }

    private fun loadExistingOrder(orderId: String) {
        screenModelScope.launch {
            getOrderByIdUseCase(orderId).collect { orderResult ->
                orderResult.ifSuccess { order ->
                    existingOrder = order.data
                    updateStateWithExistingOrder(order.data)
                    fetchAndUpdateSelectedItems(order.data.items)
                }
            }
        }
    }

    private fun updateStateWithExistingOrder(order: Order) {
        _state.value.contactPhoneField.setValue(order.contactPhone ?: "")
        _state.value.deliveryAddressField.setValue(order.address ?: "")
        _state.value.deliveryTimeField.setValue(formatFromTimestampToTime(order.pickupTime))
        _state.value.deliveryCommentField.setValue(order.comment ?: "")
        _state.update { currentState ->
            currentState.copy(
                deliveryMethod = if (order.isDelivery) DeliveryMethod.DELIVERY else DeliveryMethod.PICKUP
            )
        }
    }


    private fun fetchAndUpdateSelectedItems(orderItems: List<OrderItem>) {
        screenModelScope.launch {
            val inventoryItemIds = orderItems.map { it.inventoryId }
            getInventoryItemsByIdsUseCase(inventoryItemIds).ifSuccess { itemResult ->
                itemResult.data.forEach { item ->
                    val orderItem = orderItems.find { it.inventoryId == item.id }
                    val quantity = orderItem?.quantity ?: 0
                    updateSelectedItem(item, quantity)
                }
            }
        }
    }


    private fun submitOrder() {
        screenModelScope.launch {
            if (validateForm()) {
                val order = createOrderFromState(existingOrder)
                val result = if (existingOrder == null) {
                    createOrderUseCase(order)
                } else {
                    updateOrderUseCase(order)
                }
                result.ifSuccess {
                    showSuccessNotification(isUpdate = existingOrder != null)
                }
            }
        }
    }

    private fun showSuccessNotification(isUpdate: Boolean) {
        val (titleRes, messageRes) = if (isUpdate) {
            Pair(Res.string.order_updated, Res.string.order_updated_message)
        } else {
            Pair(Res.string.order_created, Res.string.order_created_message)
        }

        notificationManager.show(
            notification = NotificationModel.Toast(
                titleRes = titleRes,
                messageRes = messageRes,
                icon = Icons.Rounded.ShoppingCart,
                type = ToastType.Success
            )
        )
    }

    private fun formatFromTimestampToTime(timestamp: Timestamp): String {
        val instant = Instant.fromEpochMilliseconds(timestamp.seconds * MILLIS_IN_SECOND)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val hour = localDateTime.hour.toString().padStart(2, '0')
        val minute = localDateTime.minute.toString().padStart(2, '0')
        return "$hour:$minute"
    }

    private fun timestampFromDeliveryTime(time: String): Timestamp {
        val (hours, minutes) = time.split(":").map { it.toInt() }
        val localTime = LocalTime(hours, minutes)

        val selectedLocalDate = Instant.fromEpochMilliseconds(selectedDate.seconds * MILLIS_IN_SECOND)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

        val localDateTime = LocalDateTime(selectedLocalDate, localTime)
        val instant = localDateTime.toInstant(TimeZone.currentSystemDefault())

        return Timestamp(instant.epochSeconds, instant.nanosecondsOfSecond)
    }
}


