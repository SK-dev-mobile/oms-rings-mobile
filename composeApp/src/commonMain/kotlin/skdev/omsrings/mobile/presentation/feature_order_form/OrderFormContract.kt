package skdev.omsrings.mobile.presentation.feature_order_form

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource
import skdev.omsrings.mobile.domain.model.DeliveryMethod
import skdev.omsrings.mobile.presentation.feature_order_form.components.ProductSelectionEvent
import skdev.omsrings.mobile.presentation.feature_order_form.components.ProductSelectionState
import skdev.omsrings.mobile.utils.fields.FormField


class OrderFormContract {
    @Immutable
    data class State(
        val isLoading: Boolean,
        val deliveryDate: LocalDate,
        val deliveryTimeField: FormField<String, StringResource>,
        val contactPhoneField: FormField<String, StringResource>,
        val deliveryMethod: DeliveryMethod,
        val deliveryAddressField: FormField<String, StringResource>,
        val deliveryCommentField: FormField<String, StringResource>,

        // Inventory
        val productSelectionState: ProductSelectionState
    )


    sealed interface Event {
        data object OnBackClicked : Event

        // Form
        data class OnDeliveryMethodChanged(val method: DeliveryMethod) : Event

        // Submit
        object OnSubmitClicked : Event

        // Inventory
        data class OnProductSelectionEvent(val event: ProductSelectionEvent) : Event

    }

    sealed interface Effect {
        data object OrderCreated : Effect

    }
}