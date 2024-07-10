package skdev.omsrings.mobile.utils.notification

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


@Stable
class NotificationManager {
    private val _notifications = MutableStateFlow<List<NotificationModel>>(emptyList())
    val notifications: StateFlow<List<NotificationModel>> = _notifications.asStateFlow()

    fun show(notification: NotificationModel) {
        _notifications.update { currentList ->
            (currentList + notification).takeLast(2)
        }
    }

    fun dismiss(notification: NotificationModel) {
        _notifications.update { it - notification }
    }
}