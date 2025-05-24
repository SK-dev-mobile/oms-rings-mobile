package skdev.omsrings.mobile.data.repository

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseAuthInvalidCredentialsException
import dev.gitlive.firebase.auth.FirebaseAuthUserCollisionException
import dev.gitlive.firebase.firestore.DocumentReference
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import skdev.omsrings.mobile.data.base.BaseRepository
import skdev.omsrings.mobile.domain.model.UserInfo
import skdev.omsrings.mobile.domain.repository.AuthRepository
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult


class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : BaseRepository, AuthRepository {

    override val authorizedFlow: Flow<Boolean> = firebaseAuth.authStateChanged.map { user ->
        user != null
    }

    // User info
    private val userInfoCollection = firestore.collection("user_info")


    override suspend fun signIn(email: String, password: String): DataResult<Unit, DataError> =
        withCathing {
            firebaseAuth.signInWithEmailAndPassword(email, password).user?.let {
                DataResult.Success(Unit)
            } ?: DataResult.Error(DataError.Network.UNKNOWN)
        }

    override suspend fun signUp(email: String, password: String): DataResult<Unit, DataError> =
        withCathing {
            firebaseAuth.createUserWithEmailAndPassword(email, password).user?.let {
                DataResult.Success(Unit)
            } ?: DataResult.Error(DataError.Network.UNKNOWN)
        }

    override suspend fun resetPassword(email: String): DataResult<Unit, DataError> = withCathing {
        firebaseAuth.sendPasswordResetEmail(email)
        DataResult.Success(Unit)
    }

    override suspend fun addUserInfo(
        phoneNumber: String,
        fullName: String,
        isEmployer: Boolean
    ): DataResult<Unit, DataError> = withCathing {
        val user = firebaseAuth.currentUser
            ?: return@withCathing DataResult.Error(DataError.Network.UNAUTHORIZED)
        val document = firebaseAuth.currentUser?.uid?.let { userInfoCollection.document(it) }
            ?: userInfoCollection.add(user.uid)
        val snapshot = document.get()
        val oldInfo = if (snapshot.exists) {
            snapshot.data<UserInfo>()
        } else {
            UserInfo.DEFAULT
        }
        setUserInfo(
            document,
            UserInfo(
                fullName = fullName.ifBlank { oldInfo.fullName },
                phoneNumber = phoneNumber.ifBlank { oldInfo.phoneNumber },
                isEmployer = isEmployer
            )
        )
    }

    override suspend fun getUserInfo(): DataResult<UserInfo, DataError> {
        return withCathing {
            val uid =  getUserId()
            val document = uid?.let { userInfoCollection.document(it) } ?: return@withCathing DataResult.Error(DataError.Network.UNAUTHORIZED)
            Napier.d(tag = TAG) { "Getting user info for userID -> $uid " }
            val userInfo = document.get().data<UserInfo>()
            DataResult.Success(userInfo)
        }
    }

    override suspend fun isAuthorized(): DataResult<Boolean, DataError> {
        return if (firebaseAuth.currentUser != null) {
            DataResult.Success(true)
        } else {
            DataResult.Success(false)
        }
    }

    private fun getUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override suspend fun logOut(): DataResult<Unit, DataError> {
        return withCathing {
            firebaseAuth.signOut()
            DataResult.Success(Unit)
        }
    }

    private suspend fun setUserInfo(
        userInfoDocument: DocumentReference,
        userInfo: UserInfo
    ): DataResult<Unit, DataError> = withCathing {
        userInfoDocument.set(userInfo)
        DataResult.Success(Unit)
    }

    override fun Exception.toDataError(): DataError {
        return when (this) {
            is FirebaseAuthUserCollisionException -> DataError.Auth.USER_ALREADY_EXISTS
            is FirebaseAuthInvalidCredentialsException -> DataError.Auth.WRONG_CREDENTIALS
            else -> DataError.Network.UNKNOWN
        }
    }
}
