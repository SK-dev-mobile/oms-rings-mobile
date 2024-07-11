package skdev.omsrings.mobile.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSettings(
    val receiveNotifications: Boolean,
    val showClearedOrders: Boolean
) {
    companion object {
        val DEFAULT = UserSettings(
            receiveNotifications = true,
            showClearedOrders = false
        )
    }
}