package skdev.omsrings.mobile.presentation.feature_user_settings


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.clear_cancelled_orders_message
import omsringsmobile.composeapp.generated.resources.clear_cancelled_orders_title
import skdev.omsrings.mobile.app.NotificationServiceManager
import skdev.omsrings.mobile.domain.usecase.feature_user_settings.ClearOldOrdersUseCase
import skdev.omsrings.mobile.domain.usecase.feature_user_settings.GetUserSettingsUseCase
import skdev.omsrings.mobile.domain.usecase.feature_user_settings.UpdateNotificationSettingsUseCase
import skdev.omsrings.mobile.domain.usecase.feature_user_settings.UpdateShowClearedOrdersSettingsUseCase
import skdev.omsrings.mobile.domain.utils.notifyError
import skdev.omsrings.mobile.presentation.base.BaseScreenModel
import skdev.omsrings.mobile.presentation.feature_user_settings.UserSettingsContract.Effect
import skdev.omsrings.mobile.presentation.feature_user_settings.UserSettingsContract.Event
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.omsrings.mobile.utils.notification.ToastType
import skdev.omsrings.mobile.utils.result.DataResult


class UserSettingsScreenModel(
    private val notificationManager: NotificationManager,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val updateNotificationSettingsUseCase: UpdateNotificationSettingsUseCase,
    private val updateShowClearedOrdersSettingsUseCase: UpdateShowClearedOrdersSettingsUseCase,
    private val notificationServiceManager: NotificationServiceManager,
    private val clearOldOrdersUseCase: ClearOldOrdersUseCase
) : BaseScreenModel<Event, Effect>(notificationManager) {

    private val _state = MutableStateFlow(UserSettingsContract.State())
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
            when (val result = updateNotificationSettingsUseCase(newValue)) {
                is DataResult.Success -> {
                    _state.update { it.copy(receiveNotifications = newValue) }
                    notificationServiceManager.restart()
                }

                is DataResult.Error -> result.notifyError(notificationManager)
            }
        }
    }

    private fun toggleShowClearedOrders() {
        screenModelScope.launch {
            val newValue = !state.value.showClearedOrders
            when (val result = updateShowClearedOrdersSettingsUseCase(newValue)) {
                is DataResult.Success -> {
                    _state.update { it.copy(showClearedOrders = newValue) }
                }

                is DataResult.Error -> result.notifyError(notificationManager)
            }
        }
    }

    private fun clearOrders() {
        screenModelScope.launch {
            when (val result = clearOldOrdersUseCase()) {
                is DataResult.Success -> {
                    showToast(
                        titleRes = Res.string.clear_cancelled_orders_title,
                        messageRes = Res.string.clear_cancelled_orders_message,
                        icon = Icons.Rounded.Info,
                        type = ToastType.Success
                    )
                }

                is DataResult.Error -> result.notifyError(notificationManager)
            }
        }
    }

    private fun loadSettings() {
        _state.update { it.copy(isLoading = true) }
        screenModelScope.launch {
            when (val result = getUserSettingsUseCase()) {
                is DataResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            receiveNotifications = result.data.receiveNotifications,
                            showClearedOrders = result.data.showClearedOrders
                        )
                    }
                }

                is DataResult.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    result.notifyError(notificationManager)
                }
            }
        }
    }
}


