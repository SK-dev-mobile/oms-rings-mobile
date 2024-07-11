package skdev.omsrings.mobile.domain.repository

import skdev.omsrings.mobile.data.utils.DataResult
import skdev.omsrings.mobile.utils.error.DataError

interface AuthRepository {
    suspend fun signIn(login: String, password: String): DataResult<Unit, DataError>
}
