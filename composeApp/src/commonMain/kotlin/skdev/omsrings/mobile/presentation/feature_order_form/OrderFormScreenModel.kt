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

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState = _uiState.asStateFlow()

    init {
        loadInventory() // Load inventory items from the database
        orderId?.let { loadExistingOrder(it) }
    }


    private fun createInitialState() = OrderFormContract.State(
        isLoading = false,
        isEditMode = orderId != null,
        orderId = orderId,
        contactPhoneField = createFormField(Res.string.cant_be_blank),
        deliveryDate = formatFromTimestampToHumanDate(selectedDate),
        deliveryMethod = DeliveryMethod.PICKUP,
        deliveryAddressField = createFormField(Res.string.cant_be_blank),
        deliveryTimeField = createFormField(Res.string.time_cant_be_empty),
        deliveryCommentField = createFormField(),
        productSelectionState = ProductSelectionState(
            folders = emptyList(),
            selectedFolderId = null,
            selectedItemId = null,
            selectedItems = emptyMap()
        )
    )

    private fun createFormField(errorMessage: StringResource? = null) = FormField<String, StringResource>(
        scope = screenModelScope,
        initialValue = "",
        validation = flowBlock {
            if (errorMessage != null) {
                ValidationResult.of(it) {
                    notBlank(errorMessage)
                }
            } else {
                null
            }
        }
    )


    override fun onEvent(event: OrderFormContract.Event) {
        when (event) {
            is OrderFormContract.Event.OnDeliveryMethodChanged -> updateDeliveryMethod(event.method)
            is OrderFormContract.Event.OnProductSelectionEvent -> handleProductSelectionEvent(event.event)
            OrderFormContract.Event.OnSubmitClicked -> submitOrder()
            is OrderFormContract.Event.OnBackClicked -> navigateBack()
            is OrderFormContract.Event.LoadExistingOrder -> loadExistingOrder(event.orderId)

        }
    }

    private fun navigateBack() {
        screenModelScope.launch {
            launchEffect(OrderFormContract.Effect.NavigateBack)
        }
    }

    private fun handleProductSelectionEvent(event: ProductSelectionEvent) {
        when (event) {
            is ProductSelectionEvent.OnFolderSelected -> updateSelectedFolder(event.folderId)
            is ProductSelectionEvent.OnItemQuantityChanged -> updateSelectedItem(event.item, event.quantity)
        }
    }

    /**
     * Updates the selected folder in the UI state
     */
    private fun updateSelectedFolder(folderId: String?) {
        _uiState.update { it.copy(productSelectionState = it.productSelectionState.copy(selectedFolderId = folderId)) }
    }

    /**
     * Updates the quantity of the selected inventory item in the UI state
     */
    private fun updateSelectedItem(item: InventoryItem, quantity: Int) {
        _uiState.update {
            val updatedItems = _uiState.value.productSelectionState.selectedItems.toMutableMap()
            if (quantity > 0) {
                updatedItems[item] = quantity
            } else {
                updatedItems.remove(item)
            }
            it.copy(productSelectionState = it.productSelectionState.copy(selectedItems = updatedItems))
        }
    }

    private fun updateDeliveryMethod(method: DeliveryMethod) {
        _uiState.update { it.copy(deliveryMethod = method) }
    }

    private fun loadInventory() {
        screenModelScope.launch {
            getFoldersAndItemsInventory().ifSuccess { foldersAndItemsFlow ->
                foldersAndItemsFlow.data.collect { folders ->
                    _uiState.update { it.copy(productSelectionState = it.productSelectionState.copy(folders = folders)) }
                }
            }
        }
    }

    private suspend fun validateForm(): Boolean {
        val state = _uiState.value
        val phoneValid = state.contactPhoneField.validate()
        val timeValid = state.deliveryTimeField.validate()
        val commentValid = state.deliveryCommentField.validate()
        val addressValid = if (state.deliveryMethod == DeliveryMethod.DELIVERY) {
            state.deliveryAddressField.validate()
        } else true
        val productSelectionValid = state.productSelectionState.selectedItems.isNotEmpty()

        return phoneValid && timeValid && addressValid && commentValid && productSelectionValid
    }


    private fun createOrderFromState(existingOrder: Order?): Order {
        val currentTime = Timestamp.now()
        val newHistoryEvent = OrderHistoryEvent(
            time = currentTime,
            type = if (existingOrder != null) OrderHistoryEventType.CHANGED else OrderHistoryEventType.CREATED,
            user = getCurrentUserId()
        )

        val state = _uiState.value
        return Order(
            id = existingOrder?.id ?: randomUUID(),
            date = selectedDate,
            address = state.deliveryAddressField.data.value,
            comment = state.deliveryCommentField.data.value,
            contactPhone = state.contactPhoneField.data.value,
            isDelivery = state.deliveryMethod == DeliveryMethod.DELIVERY,
            pickupTime = timestampFromDeliveryTime(state.deliveryTimeField.data.value),
            status = existingOrder?.status ?: OrderStatus.CREATED,
            history = (existingOrder?.history ?: emptyList()) + newHistoryEvent,
            items = state.productSelectionState.selectedItems.map { (item, quantity) ->
                OrderItem(inventoryId = item.id, quantity = quantity)
            }
        )
    }

    private fun getCurrentUserId(): String {
        // TODO: #20 Implement actual user UUID retrieval
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
        _uiState.value.contactPhoneField.setValue(order.contactPhone ?: "")
        _uiState.value.deliveryAddressField.setValue(order.address ?: "")
        _uiState.value.deliveryTimeField.setValue(formatFromTimestampToTime(order.pickupTime))
        _uiState.value.deliveryCommentField.setValue(order.comment ?: "")
        _uiState.update { currentState ->
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

    /**
     * Formats the timestamp to a human-readable time
     * @param timestamp the timestamp to format
     * @return the formatted time string in the format "HH:mm"
     */
    private fun formatFromTimestampToTime(timestamp: Timestamp): String {
        val instant = Instant.fromEpochMilliseconds(timestamp.seconds * MILLIS_IN_SECOND)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
    }


    /**
     * Converts a time string to a timestamp
     * @param time the time string in the format "HH:mm"
     * @return the timestamp representing the time
     */
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

    /**
     * Formats the timestamp to a human-readable date
     * @param timestamp the timestamp to format
     * @return the formatted date string in the format "dd.MM.yyyy"
     */
    private fun formatFromTimestampToHumanDate(timestamp: Timestamp): String {
        val instant = Instant.fromEpochMilliseconds(timestamp.seconds * MILLIS_IN_SECOND)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${localDateTime.dayOfMonth.toString().padStart(2, '0')}.${
            localDateTime.monthNumber.toString().padStart(2, '0')
        }.${localDateTime.year}"
    }
}


