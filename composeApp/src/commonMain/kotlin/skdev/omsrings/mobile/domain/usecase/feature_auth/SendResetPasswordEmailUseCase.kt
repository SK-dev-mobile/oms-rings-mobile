package skdev.omsrings.mobile.domain.usecase.feature_auth

import skdev.omsrings.mobile.domain.repository.AuthRepository
import skdev.omsrings.mobile.domain.utils.notifyError
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.omsrings.mobile.utils.result.DataResult

class SendResetPasswordEmailUseCase(
    private val authRepository: AuthRepository,
    private val notificationManager: NotificationManager,
) {
    suspend operator fun invoke(email: String): DataResult<Unit, DataError> {
        return authRepository.resetPassword(email).notifyError(
            notificationManager
        )
    }
}
