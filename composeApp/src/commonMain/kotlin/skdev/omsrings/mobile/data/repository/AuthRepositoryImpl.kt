package skdev.omsrings.mobile.data.repository

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseAuthUserCollisionException
import dev.gitlive.firebase.auth.FirebaseAuthInvalidCredentialsException
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.UserInfo
import skdev.omsrings.mobile.data.model.UserInfoDTO
import skdev.omsrings.mobile.utils.result.DataResult
import skdev.omsrings.mobile.domain.repository.AuthRepository
import skdev.omsrings.mobile.utils.error.DataError


class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): DataResult<String, DataError> {
        return firebaseAuth.signInWithEmailAndPassword(email, password).user?.let {
            DataResult.Success(it.uid)
        } ?: DataResult.Error(DataError.Network.UNKNOWN)
    }

    override suspend fun signUp(email: String, password: String): DataResult<String, DataError> {
        try {
            return firebaseAuth.createUserWithEmailAndPassword(email, password).user?.let {
                DataResult.Success(it.uid)
            } ?: DataResult.Error(DataError.Network.UNKNOWN)
        } catch (e: FirebaseAuthUserCollisionException) {
            return DataResult.Error(DataError.Auth.USER_ALREADY_EXISTS)
        } catch (e: Exception) {
            return DataResult.Error(DataError.Auth.UNKNOWN)
        }
    }
}
