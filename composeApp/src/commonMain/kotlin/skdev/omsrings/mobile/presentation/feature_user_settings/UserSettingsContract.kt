package skdev.omsrings.mobile.presentation.feature_user_settings

class UserSettingsContract {
    data class State(
        val receiveNotifications: Boolean = false,
        val showClearedOrders: Boolean = false,
        val isLoading: Boolean = false
    )

    sealed interface Event {
        data object ToggleNotifications : Event
        data object ToggleShowClearedOrders : Event
        data object ClearOrders : Event
        data object LoadSettings : Event
    }

    sealed interface Effect {
        data class ShowClearOrdersConfirmation(val clearedCount: Int) : Effect
        data object ShowClearOrdersSuccess : Effect
        data object ShowClearOrdersError : Effect
        data class ShowError(val message: String) : Effect
    }
}