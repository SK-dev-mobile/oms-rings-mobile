package skdev.omsrings.mobile.presentation.feature_day_orders

import kotlinx.datetime.LocalDate
import skdev.omsrings.mobile.domain.model.UUID
import skdev.omsrings.mobile.presentation.feature_auth.enitity.UserRole


object DayOrdersScreenContract {
    sealed interface Event {
        data class OnStart(val selectedDate: LocalDate) : Event
        object OnDispose : Event
        data class OnNextOrderStatusClicked(val orderId: UUID) : Event
        data class OnCallClicked(val number: String) : Event
        data class OnOrderDetailsClicked(val orderId: UUID) : Event
    }

    sealed interface Effect {
        object NaivgateBack : Effect
    }
}