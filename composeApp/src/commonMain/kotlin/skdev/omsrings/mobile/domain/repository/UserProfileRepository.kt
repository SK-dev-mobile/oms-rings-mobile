package skdev.omsrings.mobile.domain.repository

import skdev.omsrings.mobile.domain.model.UserInfo
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

interface UserProfileRepository {
    suspend fun getUserProfile(): DataResult<UserInfo, DataError>
    suspend fun updateUserProfile(userInfo: UserInfo): DataResult<Unit, DataError>
    suspend fun logout(): DataResult<Unit, DataError>
}