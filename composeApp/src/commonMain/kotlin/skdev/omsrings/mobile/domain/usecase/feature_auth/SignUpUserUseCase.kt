package skdev.omsrings.mobile.domain.usecase.feature_auth

import skdev.omsrings.mobile.domain.repository.AuthRepository
import skdev.omsrings.mobile.domain.utils.notifyError
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.error.toNotificationModel
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.omsrings.mobile.utils.result.DataResult
import skdev.omsrings.mobile.utils.result.ifError

class SignUpUserUseCase(
    private val authRepository: AuthRepository,
    private val notificationManager: NotificationManager,
) {
    operator suspend fun invoke(email: String, password: String): DataResult<String, DataError> {
        return authRepository.signUp(email, password).notifyError(
            notificationManager
        )
    }
}
