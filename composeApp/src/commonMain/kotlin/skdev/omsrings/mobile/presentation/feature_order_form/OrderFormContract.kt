package skdev.omsrings.mobile.presentation.feature_order_form

import androidx.compose.runtime.Immutable
import kotlinx.datetime.Instant
import org.jetbrains.compose.resources.StringResource
import skdev.omsrings.mobile.domain.model.DeliveryMethod
import skdev.omsrings.mobile.utils.fields.FormField


class OrderFormContract {
    @Immutable
    data class State(
        val isLoading: Boolean,
        val phoneField: FormField<String, StringResource>,
        val deliveryMethod: DeliveryMethod,
        val addressField: FormField<String, StringResource>,
        val dateTimeField: FormField<String, StringResource>,
        val commentField: FormField<String, StringResource>,
    )


    sealed interface Event {
        data object OnBackClicked : Event

        // Form
        data class OnPhoneChanged(val phone: String) : Event
        data class OnDeliveryMethodChanged(val method: DeliveryMethod) : Event
        data class OnAddressChanged(val address: String) : Event
        data class OnCommentChanged(val comment: String) : Event

        data class OnDateTimeChanged(val dateTime: Instant) : Event

        // Submit
        object OnSubmitClicked : Event

    }

    sealed interface Effect {

    }
}