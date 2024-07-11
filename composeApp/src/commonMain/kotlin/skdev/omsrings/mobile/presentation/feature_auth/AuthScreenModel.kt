package skdev.omsrings.mobile.presentation.feature_auth

import skdev.omsrings.mobile.presentation.base.BaseScreenModel
import skdev.omsrings.mobile.utils.notification.NotificationManager

class AuthScreenModel(
    notificationManager: NotificationManager
) : BaseScreenModel<AuthScreenContract.Event, AuthScreenContract.Effect>(notificationManager) {
    override fun onEvent(event: AuthScreenContract.Event) {
        /* TODO: implement logic */
    }
}
