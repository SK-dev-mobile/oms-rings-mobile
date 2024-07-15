package skdev.omsrings.mobile.domain.usecase.feature_order

import skdev.omsrings.mobile.domain.model.Order
import skdev.omsrings.mobile.domain.repository.OrderRepository
import skdev.omsrings.mobile.domain.utils.notifyError
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.omsrings.mobile.utils.result.DataResult

class CreateOrderUseCase(
    private val repository: OrderRepository,
    private val notificationManager: NotificationManager
) {
    suspend operator fun invoke(order: Order): DataResult<Order, DataError> {
        return repository.createOrder(order).notifyError(
            notificationManager
        )
    }
}