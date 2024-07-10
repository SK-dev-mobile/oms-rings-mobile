package skdev.omsrings.mobile.utils.error

import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.error
import omsringsmobile.composeapp.generated.resources.no_data
import omsringsmobile.composeapp.generated.resources.no_internet
import omsringsmobile.composeapp.generated.resources.read_error
import omsringsmobile.composeapp.generated.resources.request_timeout
import omsringsmobile.composeapp.generated.resources.retry_later
import omsringsmobile.composeapp.generated.resources.serialization
import omsringsmobile.composeapp.generated.resources.server_error
import omsringsmobile.composeapp.generated.resources.unauthorized
import omsringsmobile.composeapp.generated.resources.unknown_error
import omsringsmobile.composeapp.generated.resources.user_not_found
import omsringsmobile.composeapp.generated.resources.user_not_loggined_in
import omsringsmobile.composeapp.generated.resources.write_error
import skdev.omsrings.mobile.utils.notification.NotificationModel

fun Error.toNotificationModel(): NotificationModel {
    return when (this) {
        DataError.Network.REQUEST_TIMEOUT -> NotificationModel.Error(
            titleRes = Res.string.error,
            messageRes = Res.string.request_timeout
        )
        DataError.Network.NO_INTERNET -> NotificationModel.Error(
            titleRes = Res.string.error,
            messageRes = Res.string.no_internet
        )
        DataError.Network.UNAUTHORIZED -> NotificationModel.Error(
            titleRes = Res.string.error,
            messageRes = Res.string.unauthorized
        )
        DataError.Network.SERVER_ERROR -> NotificationModel.Error(
            titleRes = Res.string.error,
            messageRes = Res.string.server_error
        )
        DataError.Network.SERIALIZATION -> NotificationModel.Error(
            titleRes = Res.string.error,
            messageRes = Res.string.serialization
        )
        DataError.Local.READ_ERROR -> NotificationModel.Error(
            titleRes = Res.string.error,
            messageRes = Res.string.read_error
        )
        DataError.Local.NO_DATA -> NotificationModel.Error(
            titleRes = Res.string.error,
            messageRes = Res.string.no_data
        )
        DataError.Local.WRITE_ERROR -> NotificationModel.Error(
            titleRes = Res.string.error,
            messageRes = Res.string.write_error
        )
        DataError.Local.USER_NOT_FOUND -> NotificationModel.Error(
            titleRes = Res.string.error,
            messageRes = Res.string.user_not_found
        )
        DataError.Local.USER_NOT_LOGGINED_IN -> NotificationModel.Error(
            titleRes = Res.string.error,
            messageRes = Res.string.user_not_loggined_in
        )
        else -> NotificationModel.Error(
            titleRes = Res.string.unknown_error,
            messageRes = Res.string.retry_later
        )
    }
}
