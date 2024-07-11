package skdev.omsrings.mobile.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.github.aakira.napier.Napier
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
        val snapshot = userSettingsDocument.get()
        return if (snapshot.exists) {
            snapshot.data(UserSettings.serializer())
        } else {
            val defaultSettings =
                UserSettings(receiveNotifications = true, showClearedOrders = false)
            userSettingsDocument.set(defaultSettings)
            defaultSettings
        }
    }

    override suspend fun updateNotificationSettings(enabled: Boolean): kotlin.Unit {
        TODO("Not yet implemented")
    }

    override suspend fun updateShowClearedOrdersSettings(show: Boolean): kotlin.Unit {
        TODO("Not yet implemented")
    }

    override suspend fun clearOldOrders(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun resetToDefault(): kotlin.Unit {
        TODO("Not yet implemented")
    }

}