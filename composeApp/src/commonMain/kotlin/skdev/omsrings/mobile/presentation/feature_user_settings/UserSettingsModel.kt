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
        screenModelScope.launch {
            val newValue = !state.value.receiveNotifications
            userSettingsRepository.updateNotificationSettings(newValue)
            _state.update { it.copy(receiveNotifications = newValue) }
        }
    }

    private fun toggleShowClearedOrders() {
        screenModelScope.launch {
            val newValue = !state.value.showClearedOrders
            userSettingsRepository.updateShowClearedOrdersSettings(newValue)
            _state.update { it.copy(showClearedOrders = newValue) }
        }
    }

    private fun clearOrders() {
        screenModelScope.launch {
            val clearedCount = userSettingsRepository.clearOldOrders()
            launchEffect(Effect.ShowClearOrdersConfirmation(clearedCount))
        }
    }

    private fun loadSettings() {
        _state.update { it.copy(isLoading = true) }
        screenModelScope.launch {
            val settings = userSettingsRepository.getUserSettings()
            _state.update {
                it.copy(
                    isLoading = false,
                    receiveNotifications = settings.receiveNotifications,
                    showClearedOrders = settings.showClearedOrders
                )
            }
        }
    }
}


