package skdev.omsrings.mobile.presentation.feature_day_orders

import cafe.adriel.voyager.core.model.screenModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import skdev.omsrings.mobile.domain.model.OrderStatus
import skdev.omsrings.mobile.domain.model.UUID
import skdev.omsrings.mobile.domain.usecase.feature_day_orders.GetDayInfoUseCase
import skdev.omsrings.mobile.domain.usecase.feature_day_orders.GetDayOrdersUseCase
import skdev.omsrings.mobile.domain.usecase.feature_day_orders.SetDayLockedStatusUseCase
import skdev.omsrings.mobile.domain.usecase.feature_day_orders.UpdateOrderStatusUseCase
import skdev.omsrings.mobile.presentation.base.BaseScreenModel
import skdev.omsrings.mobile.presentation.feature_day_orders.enitity.OrderInfoModel
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.omsrings.mobile.utils.result.ifSuccess

class DayOrdersScreenModel(
    private val getDayOrdersUseCase: GetDayOrdersUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase,
    private val getDayInfoUseCase: GetDayInfoUseCase,
    private val setDayLockedStatusUseCase: SetDayLockedStatusUseCase,
    notificationManager: NotificationManager,
    private val selectedDate: LocalDate,
) : BaseScreenModel<DayOrdersScreenContract.Event, DayOrdersScreenContract.Effect>(
    notificationManager
) {
    private val _dayOrders = MutableStateFlow(emptyList<OrderInfoModel>())
    val dayOrders = _dayOrders.asStateFlow()

    private val _isLocked = MutableStateFlow(false)
    val isLocked = _isLocked.asStateFlow()

    override fun onEvent(event: DayOrdersScreenContract.Event) {
        when (event) {
            is DayOrdersScreenContract.Event.OnCallClicked -> onCallClicked(number = event.number)

            DayOrdersScreenContract.Event.OnDispose -> {}

            is DayOrdersScreenContract.Event.OnUpdateOrderStatusClicked -> onUpdateOrderStatusClicked(
                orderId = event.orderId,
                status = event.status
            )

            is DayOrdersScreenContract.Event.OnOrderDetailsClicked -> onOrderDetailsClicked(orderId = event.orderId)
            DayOrdersScreenContract.Event.OnCreateOrderClicked -> onCreateOrderClicked()
            DayOrdersScreenContract.Event.OnStart -> onStart()
            DayOrdersScreenContract.Event.ToggleLockedStatus -> toggleLockedStatus()
        }
    }

    private fun onStart() {
        screenModelScope.launch {
            fetchData()
        }
    }

    private suspend fun fetchData() {
        startUpdating()
        getDayOrdersUseCase.invoke(selectedDate).ifSuccess { result ->
            _dayOrders.update { result.data }
        }
        getDayInfoUseCase.invoke(selectedDate).ifSuccess { result ->
            _isLocked.update { result.data.isLocked }
        }
        stopUpdating()
    }

    private fun onOrderDetailsClicked(orderId: UUID) {
        screenModelScope.launch {
            launchEffect(
                DayOrdersScreenContract.Effect.NavigateToOrderDetails(
                    selectedDate,
                    orderId
                )
            )
        }
    }

    private fun onCreateOrderClicked() {
        screenModelScope.launch {
            launchEffect(DayOrdersScreenContract.Effect.NavigateToOrderForm(selectedDate))
        }
    }

    private fun onUpdateOrderStatusClicked(orderId: UUID, status: OrderStatus) {
        screenModelScope.launch {
            Napier.d(tag = TAG) { "onUpdateOrderStatusClicked: $orderId, $status" }
            updateOrderStatusUseCase.invoke(
                orderId = orderId,
                newStatus = status
            ).ifSuccess {
                fetchData()
            }
        }
    }

    private fun onCallClicked(number: String) {
        screenModelScope.launch {
            launchEffect(DayOrdersScreenContract.Effect.IntentCallAction(number))
        }
    }

    private fun toggleLockedStatus() {
        screenModelScope.launch {
            setDayLockedStatusUseCase.invoke(selectedDate, !isLocked.value).ifSuccess {
                fetchData()
            }
        }
    }

    companion object {
        const val TAG = "DayOrdersScreenModel"
    }
}