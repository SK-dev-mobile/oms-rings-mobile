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
) : BaseScreenModel<OrderFormContract.Event, OrderFormContract.Effect>(
    notificationManager
) {

    private val _state = MutableStateFlow(
        OrderFormContract.State(
            isLoading = false,
//            showDatePicker = false,
//            showTimePicker = false,
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

    override fun onEvent(event: OrderFormContract.Event) {
        when (event) {
            is OrderFormContract.Event.OnPhoneChanged -> updatePhone(event.phone)
            is OrderFormContract.Event.OnDeliveryMethodChanged -> updateDeliveryMethod(event.method)
            is OrderFormContract.Event.OnAddressChanged -> updateAddress(event.address)
            is OrderFormContract.Event.OnCommentChanged -> TODO()
            is OrderFormContract.Event.OnBackClicked -> TODO()

            is OrderFormContract.Event.OnDateTimeChanged -> TODO()
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

    private fun updateDateTime(dateTime: Instant) {
        _state.update { it.copy(dateTimeField = it.dateTimeField.apply { setValue(dateTime.toString()) }) }
    }

    private fun handleDateSelected(selectedDate: Instant) {
        updateDateTime(selectedDate)

    }

    private fun handleTimeSelected(selectedTime: LocalTime) {
        val currentDateTime = Instant.parse(state.value.dateTimeField.data.value)
        val newDateTime = currentDateTime.toLocalDateTime(TimeZone.currentSystemDefault()).date.atTime(selectedTime)
            .toInstant(TimeZone.currentSystemDefault())
        updateDateTime(newDateTime)

    }


}