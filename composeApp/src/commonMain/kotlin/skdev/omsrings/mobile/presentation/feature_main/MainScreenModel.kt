package skdev.omsrings.mobile.presentation.feature_main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.error
import omsringsmobile.composeapp.generated.resources.user_not_found
import skdev.omsrings.mobile.presentation.base.BaseScreenModel
import skdev.omsrings.mobile.utils.notification.NotificationManager

class MainScreenModel(
    notificationManager: NotificationManager
) : BaseScreenModel<Any, Any>(notificationManager) {

    override fun onEvent(event: Any) {

    }

    fun laucnhToast() {
        showToast(
            Res.string.error,
            Res.string.user_not_found,
            Icons.Default.Info,
        )
    }
}
