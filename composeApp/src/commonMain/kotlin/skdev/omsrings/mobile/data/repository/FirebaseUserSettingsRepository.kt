package skdev.omsrings.mobile.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import skdev.omsrings.mobile.domain.model.UserSettings
import skdev.omsrings.mobile.domain.repository.UserSettingsRepository
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

class FirebaseUserSettingsRepository(
    userId: String,
    firestore: FirebaseFirestore
) : UserSettingsRepository {

    // User Settings
    private val userSettingsCollection = firestore.collection("user_settings")
    private val userSettingsDocument = userSettingsCollection.document(userId)


    private val ordersCollection = firestore.collection("orders")


    override suspend fun getUserSettings(): DataResult<UserSettings, DataError> {
        return try {
            val snapshot = userSettingsDocument.get()
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
        // Implement the logic to clear old orders
        // This is a placeholder implementation
        return DataResult.success(0)
    }

    override suspend fun resetToDefault(): DataResult<Unit, DataError> =
        setUserSettings(UserSettings.DEFAULT)

    private suspend fun updateSettings(update: (UserSettings) -> UserSettings): DataResult<Unit, DataError> {
        return try {
            when (val currentSettingsResult = getUserSettings()) {
                is DataResult.Success -> {
                    val updatedSettings = update(currentSettingsResult.data)
                    setUserSettings(updatedSettings)
                }

                is DataResult.Error -> DataResult.error(currentSettingsResult.error)
            }
        } catch (e: Exception) {
            DataResult.error(mapExceptionToDataError(e))
        }
    }

    private suspend fun setUserSettings(settings: UserSettings): DataResult<Unit, DataError> {
        return try {
            userSettingsDocument.set(settings)
            DataResult.success(Unit)
        } catch (e: Exception) {
            DataResult.error(mapExceptionToDataError(e))
        }
    }

    private fun mapExceptionToDataError(e: Exception): DataError {
        return when (e) {
//            is java.net.SocketTimeoutException -> DataError.Network.REQUEST_TIMEOUT
//            is java.io.IOException -> DataError.Network.NO_INTERNET
            else -> DataError.Network.UNKNOWN
        }
    }

}