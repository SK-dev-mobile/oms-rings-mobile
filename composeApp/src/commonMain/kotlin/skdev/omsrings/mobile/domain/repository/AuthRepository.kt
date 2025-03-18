package skdev.omsrings.mobile.domain.repository

import skdev.omsrings.mobile.presentation.feature_auth.enitity.UserRole
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

interface AuthRepository {
    suspend fun signIn(email: String, password: String): DataResult<Unit, DataError>
    suspend fun signUp(email: String, password: String): DataResult<Unit, DataError>
    suspend fun resetPassword(email: String): DataResult<Unit, DataError>
    suspend fun addUserInfo(phoneNumber: String = "", fullName: String = "", isEmployer: Boolean): DataResult<Unit, DataError>
    suspend fun isAuthorized(): DataResult<Boolean, DataError>
}
