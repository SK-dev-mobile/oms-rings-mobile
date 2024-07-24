package skdev.omsrings.mobile.data.utils

import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.fromMilliseconds
import kotlinx.datetime.LocalDate
import skdev.omsrings.mobile.utils.datetime.toInstant
import skdev.omsrings.mobile.utils.datetime.toLocalDateTime


/**
 * Получение времени начала дня.
 */
fun Timestamp.asStartOfDay(): Timestamp {
    val localDateTime = this.toLocalDateTime()
    val startOfDayInstant = localDateTime.date.toInstant(end = false)
    return Timestamp.fromMilliseconds(startOfDayInstant.toEpochMilliseconds().toDouble())
}

/**
 * Получение времени конца дня.
 */
fun Timestamp.asEndOfDay(): Timestamp {
    val localDateTime = this.toLocalDateTime()
    val startOfDayInstant = localDateTime.date.toInstant(end = true)
    return Timestamp.fromMilliseconds(startOfDayInstant.toEpochMilliseconds().toDouble())
}
