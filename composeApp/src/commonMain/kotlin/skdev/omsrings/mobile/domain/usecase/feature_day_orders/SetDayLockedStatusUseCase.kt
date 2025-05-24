package skdev.omsrings.mobile.domain.usecase.feature_day_orders

import kotlinx.datetime.LocalDate
import skdev.omsrings.mobile.data.model.DayInfoDTO
import skdev.omsrings.mobile.domain.repository.OrderRepository
import skdev.omsrings.mobile.utils.datetime.DateTimePattern
import skdev.omsrings.mobile.utils.datetime.format
import skdev.omsrings.mobile.utils.datetime.toLocalDate
import skdev.omsrings.mobile.utils.datetime.toTimestamp
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.notification.PushManager
import skdev.omsrings.mobile.utils.result.DataResult
import skdev.omsrings.mobile.utils.result.ifSuccess

class SetDayLockedStatusUseCase(
    private val orderRepository: OrderRepository,
) {
    suspend fun invoke(date: LocalDate, isLocked: Boolean): DataResult<DayInfoDTO, DataError> {
        return orderRepository.changeDayLockedStatus(
            date = date.toTimestamp(),
            isLocked = isLocked
        ).ifSuccess {
            PushManager.sendPush(
                title = "День ${
                    it.data.date.toLocalDate().format(DateTimePattern.SIMPLE_DATE)
                } ${if (!it.data.isLocked) "закрыт" else "открыт"} для заказов",
                content = "Зайдите в приложение, чтобы увидеть больше информации!"
            )
        }
    }
}
