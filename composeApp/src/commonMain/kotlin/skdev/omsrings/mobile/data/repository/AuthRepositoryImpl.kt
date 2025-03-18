package skdev.omsrings.mobile.data.repository

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseAuthInvalidCredentialsException
import dev.gitlive.firebase.auth.FirebaseAuthUserCollisionException
import dev.gitlive.firebase.firestore.DocumentReference
import dev.gitlive.firebase.firestore.FirebaseFirestore
import skdev.omsrings.mobile.data.base.BaseRepository
import skdev.omsrings.mobile.domain.model.UserInfo
import skdev.omsrings.mobile.utils.result.DataResult
import skdev.omsrings.mobile.domain.repository.AuthRepository
import skdev.omsrings.mobile.utils.error.DataError


class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : BaseRepository, AuthRepository {

    // User info
    private val userInfoCollection = firestore.collection("user_info")
    private val userInfoDocument: DocumentReference?
        get() = firebaseAuth.currentUser?.uid?.let { userInfoCollection.document(it) }


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
        val user = firebaseAuth.currentUser ?: return@withCathing DataResult.Error(DataError.Network.UNKNOWN)
        val document = userInfoDocument ?: userInfoCollection.add(user.uid)
        setUserInfo(document, UserInfo(fullName, phoneNumber, isEmployer))
    }

    override suspend fun isAuthorized(): DataResult<Boolean, DataError> {
        return if (firebaseAuth.currentUser != null) {
            DataResult.Success(true)
        } else {
            DataResult.Success(false)
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
