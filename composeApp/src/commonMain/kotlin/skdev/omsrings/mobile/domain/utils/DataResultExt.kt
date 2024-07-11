package skdev.omsrings.mobile.domain.utils

import skdev.omsrings.mobile.utils.error.Error
import skdev.omsrings.mobile.utils.error.toNotificationModel
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.omsrings.mobile.utils.result.DataResult
import skdev.omsrings.mobile.utils.result.ifError
import skdev.omsrings.mobile.utils.result.ifSuccess


suspend fun <I : Any, E : Error> DataResult<I, E>.notifyError(
    notificationManager: NotificationManager,
): DataResult<I, E> {
    return this.also {
        ifError {
            notificationManager.show(it.error.toNotificationModel())
        }
    }
}
