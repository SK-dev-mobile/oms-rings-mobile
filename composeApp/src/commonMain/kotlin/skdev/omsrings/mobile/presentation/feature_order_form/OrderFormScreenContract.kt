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
        val addressField: FormField<String, StringResource>
    )

    sealed interface Event {
        data object OnBackClicked : Event

    }

    sealed interface Effect {

    }
}