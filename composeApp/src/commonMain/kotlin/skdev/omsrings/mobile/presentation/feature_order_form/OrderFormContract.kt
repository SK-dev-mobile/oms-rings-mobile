package skdev.omsrings.mobile.presentation.feature_order_form

import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.StringResource
import skdev.omsrings.mobile.domain.model.DeliveryMethod
import skdev.omsrings.mobile.presentation.feature_order_form.components.ProductSelectionEvent
import skdev.omsrings.mobile.presentation.feature_order_form.components.ProductSelectionState
import skdev.omsrings.mobile.utils.fields.FormField


class OrderFormContract {
    @Immutable
    data class State(
        // Common
        val isLoading: Boolean,
        val isEditMode: Boolean,
        val hasUnsavedChanges: Boolean = false,

        // Form
        val orderId: String?,
        val deliveryDate: String,
        val deliveryTimeField: FormField<String, StringResource>,
        val contactPhoneField: FormField<String, StringResource>,
        val deliveryMethod: DeliveryMethod,
        val deliveryAddressField: FormField<String, StringResource>,
        val deliveryCommentField: FormField<String, StringResource>,

        // Inventory selection
        val productSelectionState: ProductSelectionState
    )


    sealed interface Event {
        data object OnBackClicked : Event
        data object OnDiscardChanges : Event
        data object OnSaveChanges : Event

        // Form
        data class OnDeliveryMethodChanged(val method: DeliveryMethod) : Event

        // Submit
        data object OnSubmitClicked : Event

        // Inventory
        data class OnProductSelectionEvent(val event: ProductSelectionEvent) : Event

        // Edit
        data class LoadExistingOrder(val orderId: String) : Event
    }

    sealed interface Effect {
        data object NavigateBack : Effect
    }
}