package skdev.omsrings.mobile.utils.datetime

import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.fromMilliseconds
import dev.gitlive.firebase.firestore.toMilliseconds
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime


/**
 * Получение местного времени LocalDateTime из строки в формате ISO.
 */
fun String.parseDate(): LocalDateTime = Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault())

/**
 * Получение абсолютного времени для даты, начала или конца дня.
 *
 * @param end - если true, то время будет концом дня, иначе началом.
 */
fun LocalDate.toInstant(end: Boolean = false): Instant =
    LocalDateTime(
        date = this,
        time = if (end) LocalTime(23, 59, 59) else LocalTime(0, 0)
    ).toInstant(TimeZone.currentSystemDefault())

/**
 * Получение абсолютного времени из миллисекунд.
 */
fun Long.toInstant(): Instant =
    Instant.fromEpochMilliseconds(this)

/**
 * Получение времени конца дня.
 */
fun Instant.toEndOfDay(): Instant =
    with(this.toLocalDateTime(TimeZone.UTC)) {
        LocalDateTime(
            date = this.date,
            time = LocalTime(23, 59, 59)
        ).toInstant(TimeZone.UTC)
    }

fun Timestamp.toInstant(): Instant =
    Instant.fromEpochMilliseconds(this.toMilliseconds().toLong())

fun Timestamp.toLocalDate(): LocalDate =
    Instant.fromEpochMilliseconds(this.toMilliseconds().toLong()).toLocalDateTime(TimeZone.currentSystemDefault()).date

fun Timestamp.toLocalDateTime(): LocalDateTime =
    Instant.fromEpochMilliseconds(this.toMilliseconds().toLong()).toLocalDateTime(TimeZone.currentSystemDefault())

fun LocalDate.toTimestamp(): Timestamp =
    Timestamp.fromMilliseconds(this.toInstant().toEpochMilliseconds().toDouble())

fun LocalDateTime.toTimestamp(): Timestamp =
    Timestamp.fromMilliseconds(this.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds().toDouble())