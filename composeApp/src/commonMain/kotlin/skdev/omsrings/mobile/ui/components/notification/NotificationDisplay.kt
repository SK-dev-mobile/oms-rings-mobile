package skdev.omsrings.mobile.ui.components.notification

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import skdev.omsrings.mobile.utils.notification.NotificationManager

@Composable
fun NotificationDisplay(
    notificationManager: NotificationManager,
    modifier: Modifier = Modifier
) {
    val notifications by notificationManager.notifications.collectAsState()
    
    Column(modifier = modifier) {
        notifications.forEach { notification ->
            key(notification) {
                NotificationItem(
                    notification = notification,
                    onDismiss = { notificationManager.dismiss(notification) }
                )
            }
        }
    }
}