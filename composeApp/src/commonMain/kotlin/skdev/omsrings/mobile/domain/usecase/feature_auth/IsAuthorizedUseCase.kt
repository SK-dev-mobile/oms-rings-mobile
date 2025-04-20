package skdev.omsrings.mobile.domain.usecase.feature_auth

import skdev.omsrings.mobile.domain.repository.AuthRepository
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

class IsAuthorizedUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): DataResult<Boolean, DataError> {
        return authRepository.isAuthorized()
    }
}