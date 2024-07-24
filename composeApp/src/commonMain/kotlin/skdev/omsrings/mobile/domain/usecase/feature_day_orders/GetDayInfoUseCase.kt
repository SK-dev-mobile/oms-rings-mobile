package skdev.omsrings.mobile.domain.usecase.feature_day_orders

import kotlinx.datetime.LocalDate
import skdev.omsrings.mobile.domain.model.DayInfoModel
import skdev.omsrings.mobile.domain.repository.OrderRepository
import skdev.omsrings.mobile.utils.datetime.toTimestamp
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

class GetDayInfoUseCase(
    private val orderRepository: OrderRepository,
) {
    suspend fun invoke(date: LocalDate): DataResult<DayInfoModel, DataError> {
        return orderRepository.getDayInfo(date.toTimestamp())
    }
}
