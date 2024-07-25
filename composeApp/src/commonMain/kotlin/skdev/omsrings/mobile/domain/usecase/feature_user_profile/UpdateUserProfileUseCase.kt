package skdev.omsrings.mobile.domain.usecase.feature_user_profile

import skdev.omsrings.mobile.domain.model.UserInfo
import skdev.omsrings.mobile.domain.repository.UserProfileRepository
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

class UpdateUserProfileUseCase(private val repository: UserProfileRepository) {
    suspend operator fun invoke(userInfo: UserInfo): DataResult<Unit, DataError> =
        repository.updateUserProfile(userInfo)
}