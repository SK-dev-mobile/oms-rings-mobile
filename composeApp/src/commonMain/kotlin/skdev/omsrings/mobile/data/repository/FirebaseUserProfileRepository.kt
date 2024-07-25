package skdev.omsrings.mobile.data.repository

import dev.gitlive.firebase.auth.FirebaseAuth
import skdev.omsrings.mobile.data.base.BaseRepository
import skdev.omsrings.mobile.data.utils.FirestoreCollections
import skdev.omsrings.mobile.domain.model.UserInfo
import skdev.omsrings.mobile.domain.repository.UserProfileRepository
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

class FirebaseUserProfileRepository(
    private val firebaseAuth: FirebaseAuth,
    firestoreCollections: FirestoreCollections
) : BaseRepository, UserProfileRepository {

    private val userInfoCollection = firestoreCollections.userInfo


    override suspend fun getUserProfile(): DataResult<UserInfo, DataError> = withCathing {
        val userId =
            firebaseAuth.currentUser?.uid ?: return@withCathing DataResult.error(DataError.Local.USER_NOT_LOGGED_IN)
        val document = userInfoCollection.document(userId).get()
        val userInfo = document.data<UserInfo>()
        userInfo.let { DataResult.Success(it) }
    }

    override suspend fun updateUserProfile(userInfo: UserInfo): DataResult<Unit, DataError> = withCathing {
        val userId =
            firebaseAuth.currentUser?.uid ?: return@withCathing DataResult.error(DataError.Local.USER_NOT_LOGGED_IN)
        userInfoCollection.document(userId).set(userInfo)
        DataResult.Success(Unit)
    }

    override suspend fun logout(): DataResult<Unit, DataError> = withCathing {
        firebaseAuth.signOut()
        DataResult.Success(Unit)
    }

    override fun Exception.toDataError(): DataError = DataError.Network.UNKNOWN
}