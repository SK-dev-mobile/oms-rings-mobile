package skdev.omsrings.mobile.presentation.feature_order_form

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.cant_be_blank
import org.jetbrains.compose.resources.StringResource
import skdev.omsrings.mobile.domain.model.DeliveryMethod
import skdev.omsrings.mobile.presentation.base.BaseScreenModel
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.flowBlock
import skdev.omsrings.mobile.utils.fields.validators.ValidationResult
import skdev.omsrings.mobile.utils.fields.validators.notBlank
import skdev.omsrings.mobile.utils.notification.NotificationManager


class OrderFormScreenModel(
    notificationManager: NotificationManager
) : BaseScreenModel<OrderFormScreenContract.Event, OrderFormScreenContract.Effect>(
    notificationManager
) {

    private val _state = MutableStateFlow(
        OrderFormScreenContract.State(
            isLoading = false,
            showDatePicker = false,
            showTimePicker = false,
            phoneField = createPhoneField(),
            deliveryMethod = DeliveryMethod.PICKUP,
            dateTimeField = createDateTimeField(),
            commentField = createCommentField(),
            addressField = createAddressField(),
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

    private fun createDateTimeField() = FormField<String, StringResource>(
        scope = screenModelScope,
        initialValue = "2023-01-02T23:40:57.120Z",
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

    override fun onEvent(event: OrderFormScreenContract.Event) {
        when (event) {
            is OrderFormScreenContract.Event.PhoneChanged -> updatePhone(event.phone)
            is OrderFormScreenContract.Event.DeliveryMethodChanged -> updateDeliveryMethod(event.method)
            is OrderFormScreenContract.Event.AddressChanged -> updateAddress(event.address)
            is OrderFormScreenContract.Event.CommentChanged -> TODO()
            is OrderFormScreenContract.Event.DateChanged -> TODO()


            is OrderFormScreenContract.Event.DateTimeFieldClicked -> showDatePicker()
            OrderFormScreenContract.Event.DismissDatePicker -> hideDatePicker()
            OrderFormScreenContract.Event.OnBackClicked -> TODO()
            is OrderFormScreenContract.Event.ConfirmTime -> updateTime(event.hour, event.minute)
            OrderFormScreenContract.Event.DismissTimePicker -> hideTimePicker()
            OrderFormScreenContract.Event.TransitionToTimePicker -> {
                hideDatePicker()
                showTimePicker()
            }
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

    private fun showDatePicker() {
        _state.update { it.copy(showDatePicker = true) }
    }

    private fun hideDatePicker() {
        _state.update { it.copy(showDatePicker = false) }
    }

    private fun showTimePicker() {
        _state.update { it.copy(showTimePicker = true) }
    }

    private fun hideTimePicker() {
        _state.update { it.copy(showTimePicker = false) }
    }

    private fun updateTime(hour: Int, minute: Int) {
        _state.update { it.copy(dateTimeField = it.dateTimeField.apply { setValue("$hour:$minute") }) }
    }

    private fun updateDateTime(dateTime: String) {
        _state.update { it.copy() }
    }

}