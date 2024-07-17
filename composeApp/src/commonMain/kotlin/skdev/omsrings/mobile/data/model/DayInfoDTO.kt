package skdev.omsrings.mobile.data.model

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable


@Serializable
data class DayInfoDTO(
    val date: Timestamp,
    val isLocked: Boolean,
)
