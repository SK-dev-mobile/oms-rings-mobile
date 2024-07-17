package skdev.omsrings.mobile.domain.usecase.feature_main

import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.fromMilliseconds
import kotlinx.datetime.LocalDate
import skdev.omsrings.mobile.domain.model.DayInfoModel
import skdev.omsrings.mobile.domain.model.Order
import skdev.omsrings.mobile.domain.repository.OrderRepository
import skdev.omsrings.mobile.domain.utils.notifyError
import skdev.omsrings.mobile.utils.datetime.toInstant
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.omsrings.mobile.utils.result.DataResult

class GetDaysInfoUseCase(
    private val repository: OrderRepository,
    private val notificationManager: NotificationManager
) {
    suspend operator fun invoke(start: LocalDate, end: LocalDate): DataResult<Map<LocalDate, DayInfoModel>, DataError> {
        return repository.getDaysInfoByRange(
            start = Timestamp.fromMilliseconds(start.toInstant().toEpochMilliseconds().toDouble()),
            end = Timestamp.fromMilliseconds(end.toInstant(end = true).toEpochMilliseconds().toDouble()),
        ).notifyError(
            notificationManager
        )
    }
}
