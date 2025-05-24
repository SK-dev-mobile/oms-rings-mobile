package skdev.omsrings.mobile.presentation.feature_day_orders

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Archive
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.order_archived_status
import omsringsmobile.composeapp.generated.resources.order_status_archived
import omsringsmobile.composeapp.generated.resources.order_status_archived_desc
import skdev.omsrings.mobile.domain.model.OrderStatus
import skdev.omsrings.mobile.domain.model.UUID
import skdev.omsrings.mobile.domain.usecase.feature_day_orders.GetDayInfoUseCase
import skdev.omsrings.mobile.domain.usecase.feature_day_orders.GetDayOrdersUseCase
import skdev.omsrings.mobile.domain.usecase.feature_day_orders.SetDayLockedStatusUseCase
import skdev.omsrings.mobile.domain.usecase.feature_day_orders.UpdateOrderStatusUseCase
import skdev.omsrings.mobile.presentation.base.BaseScreenModel
import skdev.omsrings.mobile.presentation.feature_day_orders.enitity.OrderInfoModel
import skdev.omsrings.mobile.utils.datetime.DateTimePattern
import skdev.omsrings.mobile.utils.datetime.format
import skdev.omsrings.mobile.utils.datetime.toLocalDate
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.omsrings.mobile.utils.notification.NotificationModel
import skdev.omsrings.mobile.utils.notification.PushManager
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
            val order = _dayOrders.value.find { it.id == orderId }
            if (order != null) {
                if (order.status == OrderStatus.ARCHIVED) {
                    showToast(
                        Res.string.order_status_archived,
                        Res.string.order_status_archived_desc,
                        icon = Icons.Rounded.Archive
                    )
                    return@launch
                }
            }
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
                PushManager.sendPush(
                    title = if (it.data.isLocked) "День ${it.data.date.toLocalDate().format(DateTimePattern.SIMPLE_DATE)} закрыт для заказов" else "День ${it.data.date.toLocalDate().format(DateTimePattern.SIMPLE_DATE)} отркыт для заказов",
                    content = "Зайдите в приложение, чтобы увидеть больше информации!"
                )
            }
        }
    }

    companion object {
        const val TAG = "DayOrdersScreenModel"
    }
}