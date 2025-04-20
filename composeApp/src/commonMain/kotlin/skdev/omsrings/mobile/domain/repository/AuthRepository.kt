package skdev.omsrings.mobile.domain.repository

import kotlinx.coroutines.flow.Flow
import skdev.omsrings.mobile.domain.model.UserInfo
import skdev.omsrings.mobile.presentation.feature_auth.enitity.UserRole
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

interface AuthRepository {
    suspend fun signIn(email: String, password: String): DataResult<Unit, DataError>
    suspend fun signUp(email: String, password: String): DataResult<Unit, DataError>
    suspend fun resetPassword(email: String): DataResult<Unit, DataError>
    suspend fun gerUserInfo(): DataResult<UserInfo, DataError>
    suspend fun addUserInfo(phoneNumber: String = "", fullName: String = "", isEmployer: Boolean): DataResult<Unit, DataError>
    suspend fun isAuthorized(): DataResult<Boolean, DataError>
    suspend fun logOut(): DataResult<Unit, DataError>
    val authorizedFlow: Flow<Boolean>
}
