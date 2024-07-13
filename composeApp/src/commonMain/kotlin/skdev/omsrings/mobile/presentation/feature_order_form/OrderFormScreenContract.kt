package skdev.omsrings.mobile.presentation.feature_order_form

import androidx.compose.runtime.Immutable
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
        val commentField: FormField<String, StringResource>
    )

    sealed interface Event {
        data object OnBackClicked : Event

        // Form
        data class PhoneChanged(val phone: String) : Event
        data class DeliveryMethodChanged(val method: DeliveryMethod) : Event
        data class AddressChanged(val address: String) : Event

        data class CommentChanged(val comment: String) : Event

    }

    sealed interface Effect {

    }
}