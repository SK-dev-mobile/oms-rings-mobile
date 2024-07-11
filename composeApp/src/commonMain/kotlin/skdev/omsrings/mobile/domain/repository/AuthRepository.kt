package skdev.omsrings.mobile.domain.repository

import dev.gitlive.firebase.auth.UserInfo
import skdev.omsrings.mobile.utils.result.DataResult
import skdev.omsrings.mobile.utils.error.DataError

interface AuthRepository {
    suspend fun signIn(email: String, password: String): DataResult<String, DataError>
    suspend fun signUp(email: String, password: String): DataResult<String, DataError>
}
