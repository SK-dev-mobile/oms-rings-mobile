package skdev.omsrings.mobile.presentation.feature_user_settings


import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import skdev.omsrings.mobile.domain.repository.UserSettingsRepository
import skdev.omsrings.mobile.presentation.base.BaseScreenModel
import skdev.omsrings.mobile.presentation.feature_user_settings.UserSettingsContract.Effect
import skdev.omsrings.mobile.presentation.feature_user_settings.UserSettingsContract.Event
import skdev.omsrings.mobile.utils.notification.NotificationManager


class UserSettingsModel(
    notificationManager: NotificationManager,
    private val userSettingsRepository: UserSettingsRepository
) : BaseScreenModel<Event, Effect>(notificationManager) {

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
        _state.update { it.copy(receiveNotifications = !it.receiveNotifications) }
        updateSettings()
    }

    private fun toggleShowClearedOrders() {
        _state.update { it.copy(showClearedOrders = !it.showClearedOrders) }
        updateSettings()
    }

    private fun clearOrders() {
        // Implement order clearing logic here
        // This is where you'd call a repository method to clear orders
        // For now, we'll just show a confirmation effect
        screenModelScope.launch {
            launchEffect(Effect.ShowClearOrdersConfirmation)
        }
    }

    private fun loadSettings() {
        // In a real app, you'd load settings from a repository
        // For now, we'll just simulate loading
        _state.update { it.copy(isLoading = true) }
        screenModelScope.launch {
            val userSettings = userSettingsRepository.getUserSettings()

            _state.update {
                it.copy(
                    isLoading = false,
                    receiveNotifications = userSettings.receiveNotifications,
                    showClearedOrders = userSettings.showClearedOrders
                )
            }
        }


    }

    private fun updateSettings() {
        // In a real app, you'd update settings in a repository
        // For now, we'll just simulate updating
        // You could also update the NotificationManager here if needed
    }

}


