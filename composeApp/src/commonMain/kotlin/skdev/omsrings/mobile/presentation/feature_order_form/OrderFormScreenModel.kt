package skdev.omsrings.mobile.presentation.feature_order_form

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.cant_be_blank
import org.jetbrains.compose.resources.StringResource
import skdev.omsrings.mobile.domain.model.DeliveryMethod
import skdev.omsrings.mobile.domain.model.Folder
import skdev.omsrings.mobile.domain.model.InventoryItem
import skdev.omsrings.mobile.presentation.base.BaseScreenModel
import skdev.omsrings.mobile.presentation.feature_order_form.components.ProductSelectionEvent
import skdev.omsrings.mobile.presentation.feature_order_form.components.ProductSelectionState
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.flowBlock
import skdev.omsrings.mobile.utils.fields.validators.ValidationResult
import skdev.omsrings.mobile.utils.fields.validators.notBlank
import skdev.omsrings.mobile.utils.notification.NotificationManager


class OrderFormScreenModel(
    notificationManager: NotificationManager
) : BaseScreenModel<OrderFormContract.Event, OrderFormContract.Effect>(
    notificationManager
) {

    private val _state = MutableStateFlow(
        OrderFormContract.State(
            isLoading = false,
            phoneField = createPhoneField(),
            date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            deliveryMethod = DeliveryMethod.PICKUP,
            commentField = createCommentField(),
            addressField = createAddressField(),
            timeField = createTimeField(),
            productSelectionState = ProductSelectionState(
                folders = listOf(
                    Folder(
                        id = "1",
                        name = "Folder 1",
                        inventoryItems = listOf(
                            InventoryItem(
                                id = "1",
                                name = "Item 1",
                                stockQuantity = 10
                            ),
                            InventoryItem(
                                id = "2",
                                name = "Item 2",
                                stockQuantity = 20
                            ),
                        )
                    ),
                    Folder(
                        id = "2",
                        name = "Folder 2",
                        inventoryItems = listOf(
                            InventoryItem(
                                id = "3",
                                name = "Item 3",
                                stockQuantity = 30
                            ),
                            InventoryItem(
                                id = "4",
                                name = "Item 4",
                                stockQuantity = 40
                            ),
                        )
                    ),
                ),
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

    private fun createTimeField() = FormField<String, StringResource>(
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

    private fun createDateTimeField() = FormField<String, StringResource>(
        scope = screenModelScope,
        initialValue = "",
        validation = flowBlock {
            ValidationResult.of(it) {
                notBlank(Res.string.cant_be_blank)
            }
        }
    )

    private fun createCommentField() = FormField<String, StringResource>(
        scope = screenModelScope,
        initialValue = "",
        validation = flowBlock {
            ValidationResult.of(it) {
                notBlank(Res.string.cant_be_blank)
            }
        }
    )

    override fun onEvent(event: OrderFormContract.Event) {
        when (event) {
            is OrderFormContract.Event.OnPhoneChanged -> updatePhone(event.phone)
            is OrderFormContract.Event.OnDeliveryMethodChanged -> updateDeliveryMethod(event.method)
            is OrderFormContract.Event.OnAddressChanged -> updateAddress(event.address)
            is OrderFormContract.Event.OnCommentChanged -> TODO()
            is OrderFormContract.Event.OnBackClicked -> TODO()

            OrderFormContract.Event.OnSubmitClicked -> TODO()
            is OrderFormContract.Event.OnProductSelectionEvent -> handleProductSelectionEvent(event.event)
            is OrderFormContract.Event.OnTimeChanged -> updateTime(event.time)
        }
    }

    private fun handleProductSelectionEvent(event: ProductSelectionEvent) {
        when (event) {
            is ProductSelectionEvent.OnFolderSelected -> updateSelectedFolder(event.folderId)
            is ProductSelectionEvent.OnItemQuantityChanged -> updateItemQuantity(event.item, event.quantity)
        }
    }

    private fun updateSelectedFolder(folderId: String?) {
        _state.update { it.copy(productSelectionState = it.productSelectionState.copy(selectedFolderId = folderId)) }
    }

    private fun updateItemQuantity(item: InventoryItem, quantity: Int) {
        _state.update {
            it.copy(
                productSelectionState = it.productSelectionState.copy(
                    selectedItems = it.productSelectionState.selectedItems.toMutableMap().apply {
                        put(item, quantity)
                    }
                )
            )
        }
    }


    private fun updatePhone(phone: String) {
        _state.update { it.copy(phoneField = it.phoneField.apply { setValue(phone) }) }
    }

    private fun updateDeliveryMethod(method: DeliveryMethod) {
        _state.update { it.copy(deliveryMethod = method) }
    }

    private fun updateAddress(newAddress: String) {
        _state.update { it.copy(addressField = it.addressField.apply { setValue(newAddress) }) }
    }

    private fun updateTime(time: LocalTime) {
        _state.update { it.copy(timeField = it.timeField.apply { setValue(time.toString()) }) }
    }


}