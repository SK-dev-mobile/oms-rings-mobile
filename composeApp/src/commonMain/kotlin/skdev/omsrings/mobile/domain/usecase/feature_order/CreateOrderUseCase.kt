package skdev.omsrings.mobile.domain.usecase.feature_order

import skdev.omsrings.mobile.domain.model.Order
import skdev.omsrings.mobile.domain.repository.OrderRepository
import skdev.omsrings.mobile.domain.utils.notifyError
import skdev.omsrings.mobile.utils.datetime.DateTimePattern
import skdev.omsrings.mobile.utils.datetime.format
import skdev.omsrings.mobile.utils.datetime.toLocalDate
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.format.OrderIdFormatter
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.omsrings.mobile.utils.notification.PushManager
import skdev.omsrings.mobile.utils.result.DataResult
import skdev.omsrings.mobile.utils.result.ifSuccess

class CreateOrderUseCase(
    private val repository: OrderRepository,
    private val notificationManager: NotificationManager
) {
    suspend operator fun invoke(order: Order): DataResult<Order, DataError> {
        return repository.createOrder(order).notifyError(
            notificationManager
        ).ifSuccess {
            PushManager.sendPush(
                title = "Поступил заказ на ${it.data.date.toLocalDate().format(DateTimePattern.SIMPLE_DATE)}",
                content = "Пользователь ${it.data.createdBy} разместил(а) новый заказ: ${OrderIdFormatter.getFirstPart(it.data.id)}..."
            )
        }
    }
}