package skdev.omsrings.mobile.data.repository

import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import skdev.omsrings.mobile.data.utils.DataResult
import skdev.omsrings.mobile.domain.repository.AuthRepository
import skdev.omsrings.mobile.utils.error.DataError

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun signIn(login: String, password: String): DataResult<Unit, DataError> {
        /* TODO: Implement */
        return DataResult.Success(Unit)
    }

}
