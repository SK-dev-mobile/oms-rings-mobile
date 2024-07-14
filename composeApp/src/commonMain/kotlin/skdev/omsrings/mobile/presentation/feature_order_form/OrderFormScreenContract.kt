package skdev.omsrings.mobile.presentation.feature_order_form

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.StringResource
import skdev.omsrings.mobile.domain.model.DeliveryMethod
import skdev.omsrings.mobile.utils.fields.FormField


class OrderFormScreenContract {
    @Immutable
    data class State(
        val isLoading: Boolean,
        val phoneField: FormField<String, StringResource>,
        val deliveryMethod: DeliveryMethod,
        val addressField: FormField<String, StringResource>,
        val dateTimeField: FormField<String, StringResource>,
        val commentField: FormField<String, StringResource>,

        // Dialog
        val showDatePicker: Boolean,
        val showTimePicker: Boolean,
    )

    sealed interface Event {
        data object OnBackClicked : Event

        // Form
        data class PhoneChanged(val phone: String) : Event
        data class DeliveryMethodChanged(val method: DeliveryMethod) : Event
        data class AddressChanged(val address: String) : Event
        data class DateTimeChanged(val date: String) : Event
        data class CommentChanged(val comment: String) : Event

        // Date Picker
        data object DateTimeFieldClicked : Event
        data object DismissDatePicker : Event
        data class DateSelected(val date: LocalDate) : Event


        // Time Picker
        data object DismissTimePicker : Event
        data class TimeSelected(val time: LocalTime) : Event
    }

    sealed interface Effect {

    }
}