package skdev.omsrings.mobile.presentation.feature_order_form

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.StringResource
import skdev.omsrings.mobile.domain.model.DeliveryMethod
import skdev.omsrings.mobile.presentation.feature_order_form.components.ProductSelectionEvent
import skdev.omsrings.mobile.presentation.feature_order_form.components.ProductSelectionState
import skdev.omsrings.mobile.utils.fields.FormField


class OrderFormContract {
    @Immutable
    data class State(
        val isLoading: Boolean,
        val date: LocalDate,
        val timeField: FormField<String, StringResource>,
        val phoneField: FormField<String, StringResource>,
        val deliveryMethod: DeliveryMethod,
        val addressField: FormField<String, StringResource>,
        val commentField: FormField<String, StringResource>,

        // Inventory
        val productSelectionState: ProductSelectionState
    )


    sealed interface Event {
        data object OnBackClicked : Event

        // Form
        data class OnPhoneChanged(val phone: String) : Event
        data class OnDeliveryMethodChanged(val method: DeliveryMethod) : Event
        data class OnAddressChanged(val address: String) : Event
        data class OnCommentChanged(val comment: String) : Event
        data class OnTimeChanged(val time: String) : Event

        // Submit
        object OnSubmitClicked : Event

        // Inventory
        data class OnProductSelectionEvent(val event: ProductSelectionEvent) : Event

    }

    sealed interface Effect {

    }
}