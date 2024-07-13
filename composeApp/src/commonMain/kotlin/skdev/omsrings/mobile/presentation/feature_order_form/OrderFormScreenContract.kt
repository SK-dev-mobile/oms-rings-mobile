package skdev.omsrings.mobile.presentation.feature_order_form

import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.StringResource
import skdev.omsrings.mobile.utils.fields.FormField


class OrderFormScreenContract {
    @Immutable
    data class State(
        val phoneField: FormField<String, StringResource>
    )

    sealed interface Event {

    }

    sealed interface Effect {

    }
}