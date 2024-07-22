package skdev.omsrings.mobile.presentation.feature_day_orders

import skdev.omsrings.mobile.domain.model.UUID
import skdev.omsrings.mobile.presentation.feature_auth.enitity.UserRole


object DayOrdersScreenContract {
    sealed interface Event {
        object OnStart : Event
        object OnDispose : Event
        data class OnNextOrderStatusClicked(val orderId: UUID) : Event
        data class OnCallClicked(val number: String) : Event
        data class OnOrderDetailsClicked(val orderId: UUID) : Event
    }

    sealed interface Effect {
        object NaivgateBack : Effect
    }
}