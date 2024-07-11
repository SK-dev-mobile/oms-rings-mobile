package skdev.omsrings.mobile.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSettings(
    val receiveNotifications: Boolean,
    val showClearedOrders: Boolean
)