package skdev.omsrings.mobile.presentation.feature_day_orders

import kotlinx.datetime.LocalDate
import skdev.omsrings.mobile.domain.model.OrderStatus
import skdev.omsrings.mobile.domain.model.UUID
import skdev.omsrings.mobile.presentation.feature_auth.enitity.UserRole


object DayOrdersScreenContract {
    sealed interface Event {
        object OnStart : Event
        object OnDispose : Event
        data class OnUpdateOrderStatusClicked(val orderId: UUID, val status: OrderStatus) : Event
        data class OnCallClicked(val number: String) : Event
        data class OnOrderDetailsClicked(val orderId: UUID) : Event
        object OnCreateOrderClicked : Event
    }

    sealed interface Effect {
        object NaivgateBack : Effect
        data class NavigateToOrderDetails(val selectedDate: LocalDate, val orderId: UUID) : Effect
        data class NavigateToOrderForm(val selectedDate: LocalDate) : Effect
    }
}