package skdev.omsrings.mobile.domain.usecase.feature_day_orders

import kotlinx.datetime.LocalDate
import skdev.omsrings.mobile.data.model.DayInfoDTO
import skdev.omsrings.mobile.domain.repository.OrderRepository
import skdev.omsrings.mobile.utils.datetime.toTimestamp
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

class SetDayLockedStatusUseCase(
    private val orderRepository: OrderRepository,
) {
    suspend fun invoke(date: LocalDate, isLocked: Boolean): DataResult<DayInfoDTO, DataError> {
        return orderRepository.changeDayLockedStatus(
            date = date.toTimestamp(),
            isLocked = isLocked
        )
    }
}
