package skdev.omsrings.mobile.presentation.feature_user_settings

class UserSettingsContract {
    data class State(
        val receiveNotifications: Boolean = false,
        val showClearedOrders: Boolean = false,
        val isLoading: Boolean = false
    )

    sealed interface Event {
        object ToggleNotifications : Event
        object ToggleShowClearedOrders : Event
        object ClearOrders : Event
        object LoadSettings : Event
    }

    sealed interface Effect {
        object ShowClearOrdersConfirmation : Effect
        object ShowClearOrdersSuccess : Effect
        object ShowClearOrdersError : Effect
        data class ShowError(val message: String) : Effect
    }
}