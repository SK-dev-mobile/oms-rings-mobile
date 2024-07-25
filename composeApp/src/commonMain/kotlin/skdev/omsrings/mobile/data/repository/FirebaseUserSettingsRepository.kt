package skdev.omsrings.mobile.data.repository

import skdev.omsrings.mobile.data.utils.FirestoreCollections
import skdev.omsrings.mobile.domain.model.UserSettings
import skdev.omsrings.mobile.domain.repository.UserSettingsRepository
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

class FirebaseUserSettingsRepository(
    userId: String,
    private val firestoreCollections: FirestoreCollections
) : UserSettingsRepository {

    // User Settings
    private val userSettingsCollection = firestoreCollections.userSettings
    private val userSettingsDocument = userSettingsCollection.document(userId)

    private val ordersCollection = firestoreCollections.orders


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
        // TODO: Implement the logic to clear old orders
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
        // TODO: Дополнить функцию mapExceptionToDataError для обработки различных типов исключений

        return when (e) {
//            is java.net.SocketTimeoutException -> DataError.Network.REQUEST_TIMEOUT
//            is java.io.IOException -> DataError.Network.NO_INTERNET
            else -> DataError.Network.UNKNOWN
        }
    }

}