package skdev.omsrings.mobile.presentation.feature_order_form

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShoppingCart
import cafe.adriel.voyager.core.model.screenModelScope
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.cant_be_blank
import omsringsmobile.composeapp.generated.resources.order_created
import omsringsmobile.composeapp.generated.resources.order_created_message
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


class OrderFormScreenModel(
    private val notificationManager: NotificationManager,
    private val createOrderUseCase: CreateOrderUseCase,
    private val getFoldersAndItemsInventory: GetFoldersAndItemsInventory,
    private val selectedDate: Timestamp
) : BaseScreenModel<OrderFormContract.Event, OrderFormContract.Effect>(
    notificationManager
) {

    private val _state = MutableStateFlow(
        OrderFormContract.State(
            isLoading = false,
            contactPhoneField = createPhoneField(),
            deliveryDate = "15.07.2024",
            deliveryMethod = DeliveryMethod.PICKUP,
            deliveryAddressField = createAddressField(),
            deliveryTimeField = createTimeField(),
            deliveryCommentField = createCommentField(),
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
    }

    override fun onEvent(event: OrderFormContract.Event) {
        when (event) {
            is OrderFormContract.Event.OnDeliveryMethodChanged -> updateDeliveryMethod(event.method)
            is OrderFormContract.Event.OnProductSelectionEvent -> handleProductSelectionEvent(event.event)
            OrderFormContract.Event.OnSubmitClicked -> submitOrder()
            is OrderFormContract.Event.OnBackClicked -> TODO()

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

    private fun createOrderFromState(): Order {
        return Order(
            id = randomUUID(),
            date = selectedDate,
            address = _state.value.deliveryAddressField.data.value,
            comment = _state.value.deliveryCommentField.data.value,
            contactPhone = _state.value.contactPhoneField.data.value,
            isDelivery = _state.value.deliveryMethod == DeliveryMethod.DELIVERY,
            pickupTime = Timestamp.now(),
            status = OrderStatus.CREATED,
            history = listOf(
                OrderHistoryEvent(
                    time = Timestamp.now(),
                    type = OrderHistoryEventType.CREATED,
                    user = getCurrentUserId()
                )
            ),
            items = _state.value.productSelectionState.selectedItems.map { (item, quantity) ->
                OrderItem(inventoryId = item.id, quantity = quantity)
            }
        )
    }

    private fun getCurrentUserId(): String {
        return "current_user_id"
    }

    private fun submitOrder() {
        screenModelScope.launch {
            if (validateForm()) {
                val order = createOrderFromState()
                createOrderUseCase(order).ifSuccess {
                    notificationManager.show(
                        notification = NotificationModel.Toast(
                            titleRes = Res.string.order_created,
                            messageRes = Res.string.order_created_message,
                            icon = Icons.Rounded.ShoppingCart,
                            type = ToastType.Success
                        )
                    )
                }
            }
        }
    }
}
