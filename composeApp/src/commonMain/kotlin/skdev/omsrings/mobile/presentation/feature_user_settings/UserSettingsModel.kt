package skdev.omsrings.mobile.presentation.feature_user_settings


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import skdev.omsrings.mobile.di.UserSettingsRepository
import skdev.omsrings.mobile.presentation.base.BaseScreenModel
import skdev.omsrings.mobile.presentation.feature_user_settings.UserSettingsContract.Event
import skdev.omsrings.mobile.utils.notification.NotificationManager


class UserSettingsModel(
    notificationManager: NotificationManager,
    userSettingsRepository: UserSettingsRepository
) : BaseScreenModel<Event, UserSettingsContract.Effect>(notificationManager) {

    private val _state = MutableStateFlow<UserSettingsContract.State>(UserSettingsContract.State())
    val state = _state.asStateFlow()

    init {
        onEvent(Event.LoadSettings)
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.ToggleNotifications -> toggleNotifications()
            is Event.ToggleShowClearedOrders -> toggleShowClearedOrders()
            is Event.ClearOrders -> clearOrders()
            is Event.LoadSettings -> loadSettings()
        }
    }

    private fun toggleNotifications() {
        TODO("Not yet implemented")
    }

    private fun toggleShowClearedOrders() {
        TODO("Not yet implemented")
    }

    private fun clearOrders() {
        TODO("Not yet implemented")
    }

    private fun loadSettings() {
        _state.update { it.copy(isLoading = true) }

        // TODO: Load settings from repository

        _state.update {
            it.copy(
                isLoading = false,
                receiveNotifications = true,
                showClearedOrders = false
            )
        }

    }

    private fun updateSettings() {
        TODO("Not yet implemented")
    }

}


