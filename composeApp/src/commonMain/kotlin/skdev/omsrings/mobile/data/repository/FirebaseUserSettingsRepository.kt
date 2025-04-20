package skdev.omsrings.mobile.data.repository

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import skdev.omsrings.mobile.domain.model.UserSettings
import skdev.omsrings.mobile.domain.repository.UserSettingsRepository
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

class FirebaseUserSettingsRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : UserSettingsRepository {

    // User Settings
    private val userSettingsCollection = firestore.collection("user_settings")
    private suspend fun getUserId(): String? = firebaseAuth.currentUser?.uid
    private suspend fun getUserSettingsDocument() = getUserId()?.let {
        userSettingsCollection.document(it)
    }


    private val ordersCollection = firestore.collection("orders")


    override suspend fun getUserSettings(): DataResult<UserSettings, DataError> {
        return try {
            val document = getUserSettingsDocument() ?: return DataResult.error(DataError.Local.USER_NOT_LOGGED_IN)

            val snapshot = document.get()
            val settings = if (snapshot.exists) {
                snapshot.data(UserSettings.serializer())
            } else {
                UserSettings.DEFAULT.also { setUserSettings(it) }
            }
            DataResult.success(settings)
        } catch (e: Exception) {
            DataResult.error(mapExceptionToDataError(e))
        }
    }

    override suspend fun updateNotificationSettings(enabled: Boolean): DataResult<Unit, DataError> =
        updateSettings { it.copy(receiveNotifications = enabled) }

    override suspend fun updateShowClearedOrdersSettings(show: Boolean): DataResult<Unit, DataError> =
        updateSettings { it.copy(showClearedOrders = show) }

    override suspend fun clearOldOrders(): DataResult<Int, DataError> {
        // Implementation remains a TODO, but we'll add a proper error
        return DataResult.error(DataError.Feature.NOT_IMPLEMENTED)
    }

    override suspend fun resetToDefault(): DataResult<Unit, DataError> =
        setUserSettings(UserSettings.DEFAULT)

    private suspend fun updateSettings(update: (UserSettings) -> UserSettings): DataResult<Unit, DataError> {
        return try {
            val document = getUserSettingsDocument() ?: return DataResult.error(DataError.Local.USER_NOT_LOGGED_IN)

            val currentSettings = document.get().data(UserSettings.serializer())
            val updatedSettings = update(currentSettings)
            setUserSettings(updatedSettings)
        } catch (e: Exception) {
            DataResult.error(mapExceptionToDataError(e))
        }
    }

    private suspend fun setUserSettings(settings: UserSettings): DataResult<Unit, DataError> {
        return try {
            val document = getUserSettingsDocument() ?: return DataResult.error(DataError.Local.USER_NOT_LOGGED_IN)

            document.set(settings)
            DataResult.success(Unit)
        } catch (e: Exception) {
            DataResult.error(mapExceptionToDataError(e))
        }
    }

    private fun mapExceptionToDataError(e: Exception): DataError {
        return when (e) {
            else -> DataError.Network.UNKNOWN
        }
    }
}