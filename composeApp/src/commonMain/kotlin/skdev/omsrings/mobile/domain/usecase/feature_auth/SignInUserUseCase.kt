package skdev.omsrings.mobile.domain.usecase.feature_auth

import skdev.omsrings.mobile.utils.result.DataResult
import skdev.omsrings.mobile.utils.result.ifError
import skdev.omsrings.mobile.domain.repository.AuthRepository
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.error.toNotificationModel
import skdev.omsrings.mobile.utils.notification.NotificationManager


class SignInUserUseCase(
    private val authRepository: AuthRepository,
    private val notificationManager: NotificationManager,
) {
    operator suspend fun invoke(email: String, password: String): DataResult<String, DataError> {
        return authRepository.signIn(email, password).also {
            it.ifError {
                notificationManager.show(it.error.toNotificationModel())
            }
        }
    }
}
