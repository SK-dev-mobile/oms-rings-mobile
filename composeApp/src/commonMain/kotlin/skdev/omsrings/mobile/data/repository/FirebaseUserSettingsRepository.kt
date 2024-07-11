package skdev.omsrings.mobile.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import skdev.omsrings.mobile.domain.model.UserSettings
import skdev.omsrings.mobile.domain.repository.UserSettingsRepository

class FirebaseUserSettingsRepository(
    private val userId: String,
    private val firestore: FirebaseFirestore
) : UserSettingsRepository {

    // User Settings
    private val userSettingsCollection = firestore.collection("user_settings")
    private val userSettingsDocument = userSettingsCollection.document(userId)


    private val ordersCollection = firestore.collection("orders")


    override suspend fun getUserSettings(): UserSettings {
        return userSettingsDocument.get().let { snapshot ->
            if (snapshot.exists) {
                snapshot.data(UserSettings.serializer())
            } else {
                UserSettings.DEFAULT.also { setUserSettings(it) }
            }
        }
    }

    override suspend fun updateNotificationSettings(enabled: Boolean) {
        updateSettings { it.copy(receiveNotifications = enabled) }
    }

    override suspend fun updateShowClearedOrdersSettings(show: Boolean) {
        updateSettings { it.copy(showClearedOrders = show) }
    }

    override suspend fun clearOldOrders(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun resetToDefault() {
        setUserSettings(UserSettings.DEFAULT)
    }

    private suspend fun updateSettings(update: (UserSettings) -> UserSettings) {
        val currentSettings = getUserSettings()
        val updatedSettings = update(currentSettings)
        setUserSettings(updatedSettings)
    }

    private suspend fun setUserSettings(settings: UserSettings) {
        userSettingsDocument.set(settings)
    }

}