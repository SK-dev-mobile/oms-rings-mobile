package skdev.omsrings.mobile.presentation.feature_main

import cafe.adriel.voyager.core.model.screenModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import skdev.omsrings.mobile.domain.usecase.feature_auth.GetUserInfoUseCase
import skdev.omsrings.mobile.domain.usecase.feature_main.GetDaysInfoUseCase
import skdev.omsrings.mobile.presentation.base.BaseScreenModel
import skdev.omsrings.mobile.presentation.feature_main.components.YearMonth
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.omsrings.mobile.utils.result.ifSuccess
import kotlin.time.Duration.Companion.seconds

class MainScreenModel(
    notificationManager: NotificationManager,
    private val getDaysInfoUseCase: GetDaysInfoUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
) : BaseScreenModel<MainScreenContract.Event, MainScreenContract.Effect>(notificationManager) {

    private val _uiState = MutableStateFlow(MainScreenContract.State())
    val uiState = _uiState.asStateFlow()

    override fun onEvent(event: MainScreenContract.Event) {
        when(event) {
            MainScreenContract.Event.OnStart -> {}
            MainScreenContract.Event.OnDispose -> {}
            is MainScreenContract.Event.OnLoadMonthInfo -> {
                onLoadMonthInfo(event.month)
            }
        }
    }

    private fun onLoadMonthInfo(month: YearMonth) {
        screenModelScope.launch {
            startUpdating()
            Napier.d(tag = TAG) { "Load days info from ${month.firstDayOfMonth} to ${month.lastDayOfMonth}" }

            getUserInfoUseCase().ifSuccess { result ->
                Napier.d(tag = TAG) { "User info loaded: ${result.data}" }
                _uiState.update {
                    it.copy(isEmployer = result.data.isEmployer)
                }
            }

            getDaysInfoUseCase(
                start = month.firstDayOfMonth,
                end = month.lastDayOfMonth,
            ).ifSuccess { result ->
                Napier.d(tag = TAG) { "Days info loaded: ${result.data}" }
                _uiState.update {
                    it.copy(
                        calendarDays = result.data,
                    )
                }
            }
            delay(500L) // Ожидание, чтобы показывать progress bar дольше, чем реальная загрузка
            stopUpdating()
        }
    }

    companion object {
        const val TAG = "MainScreenModel"
    }
}
