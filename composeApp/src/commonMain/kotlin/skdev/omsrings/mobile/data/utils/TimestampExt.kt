package skdev.omsrings.mobile.data.utils

import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.toMilliseconds
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


fun Timestamp.toLocalDate(): LocalDate =
    Instant.fromEpochMilliseconds(this.toMilliseconds().toLong()).toLocalDateTime(TimeZone.currentSystemDefault()).date


fun Timestamp.toLocalDateTime(): LocalDateTime =
    Instant.fromEpochMilliseconds(this.toMilliseconds().toLong()).toLocalDateTime(TimeZone.currentSystemDefault())
