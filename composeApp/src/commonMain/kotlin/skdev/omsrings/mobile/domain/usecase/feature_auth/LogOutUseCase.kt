package skdev.omsrings.mobile.domain.usecase.feature_auth

import skdev.omsrings.mobile.domain.repository.AuthRepository

class LogOutUseCase (
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.logOut()
    }
}