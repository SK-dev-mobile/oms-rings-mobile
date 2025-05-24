package skdev.omsrings.mobile.domain.usecase.feature_day_orders

import dev.gitlive.firebase.firestore.Timestamp
import io.github.aakira.napier.Napier
import skdev.omsrings.mobile.domain.model.OrderHistoryEvent
import skdev.omsrings.mobile.domain.model.OrderHistoryEventType
import skdev.omsrings.mobile.domain.model.OrderStatus
import skdev.omsrings.mobile.domain.repository.AuthRepository
import skdev.omsrings.mobile.domain.repository.OrderRepository
import skdev.omsrings.mobile.domain.utils.notifyError
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.omsrings.mobile.utils.result.DataResult
import skdev.omsrings.mobile.utils.result.ifError
import skdev.omsrings.mobile.utils.result.ifSuccess

class UpdateOrderStatusUseCase(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository,
    private val notificationManager: NotificationManager,
) {
    suspend fun invoke(orderId: String, newStatus: OrderStatus): DataResult<Unit, DataError> {
        val userInfoResult = authRepository.getUserInfo().notifyError(
            notificationManager
        ).ifError {
            return DataResult.error(it.error)
        }

        check(userInfoResult is DataResult.Success)

        Napier.d(tag = TAG) { "User info: ${userInfoResult.data}" }

        orderRepository.getOrderById(orderId).notifyError(
            notificationManager
        ).ifSuccess {
            orderRepository.updateOrder(
                it.data.copy(
                    status = newStatus,
                    history = it.data.history.plus(
                        OrderHistoryEvent(
                            time = Timestamp.now(),
                            type = when(newStatus) {
                                OrderStatus.CREATED -> OrderHistoryEventType.CREATED
                                OrderStatus.COMPLETED -> OrderHistoryEventType.COMPLETED
                                OrderStatus.ARCHIVED -> OrderHistoryEventType.ARCHIVED
                            },
                            userFullName = userInfoResult.data.fullName
                        )
                    )
                )
            )
        }.ifError {
            return DataResult.error(it.error)
        }

        return DataResult.success(Unit)
    }

    companion object {
        const val TAG = "UpdateOrderStatusUseCase"
    }
}
