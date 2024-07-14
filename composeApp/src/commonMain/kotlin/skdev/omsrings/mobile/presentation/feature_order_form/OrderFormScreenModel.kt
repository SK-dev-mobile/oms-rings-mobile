package skdev.omsrings.mobile.presentation.feature_order_form

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
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
        initialValue = "2024-07-12T12:00:00.0Z",
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

       
            is OrderFormScreenContract.Event.DateTimeFieldClicked -> showDatePicker()
            is OrderFormScreenContract.Event.DismissDatePicker -> hideDatePicker()
            is OrderFormScreenContract.Event.OnBackClicked -> TODO()
            is OrderFormScreenContract.Event.DismissTimePicker -> hideTimePicker()

            is OrderFormScreenContract.Event.DateSelected -> handleDateSelected(event.date)
            is OrderFormScreenContract.Event.TimeSelected -> handleTimeSelected(event.time)
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

    private fun updateDateTime(dateTime: Instant) {
        _state.update { it.copy(dateTimeField = it.dateTimeField.apply { setValue(dateTime.toString()) }) }
    }

    private fun handleDateSelected(selectedDate: Instant) {
        updateDateTime(selectedDate)
        hideDatePicker()
        showTimePicker()
    }

    private fun handleTimeSelected(selectedTime: LocalTime) {
        val currentDateTime = Instant.parse(state.value.dateTimeField.data.value)
        val newDateTime = currentDateTime.toLocalDateTime(TimeZone.currentSystemDefault()).date.atTime(selectedTime)
            .toInstant(TimeZone.currentSystemDefault())
        updateDateTime(newDateTime)
        hideTimePicker()
    }


}