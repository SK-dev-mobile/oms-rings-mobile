package skdev.omsrings.mobile.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class DayInfoModel(
    val isEdited: Boolean,
    val isLocked: Boolean,
)
