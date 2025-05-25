package skdev.omsrings.mobile.app

import com.mmk.kmpnotifier.notification.NotifierManager
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import skdev.omsrings.mobile.domain.usecase.feature_user_settings.GetUserSettingsUseCase
import skdev.omsrings.mobile.utils.result.ifSuccess
import kotlin.coroutines.CoroutineContext

class NotificationServiceManager(
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
): KoinComponent, CoroutineScope {
    override val coroutineContext: CoroutineContext = Job() + Dispatchers.IO
    
    private val isTokenReady = MutableStateFlow(false)
    private val isInitialized = MutableStateFlow(false)

    init {
        Napier.d(tag = "NotificationServiceManager") { "NotificationServiceManager initialized" }
        setupNotificationListener()
    }

    private fun setupNotificationListener() {
        NotifierManager.addListener(object : NotifierManager.Listener {
            override fun onNewToken(token: String) {
                Napier.d(tag = "NotificationServiceManager") { "New token received: $token" }
                isTokenReady.value = true
                if (!isInitialized.value) {
                    isInitialized.value = true
                    launch {
                        restart()
                    }
                }
            }

            override fun onPushNotification(title: String?, body: String?) {
                Napier.d(tag = "NotificationServiceManager") { "Push received: $title, $body" }
            }
        })
    }

    suspend fun restart() {
        Napier.d(tag = "NotificationServiceManager") { "Restarting NotificationServiceManager" }
        if (!isTokenReady.value) {
            Napier.d(tag = "NotificationServiceManager") { "Token not ready yet, waiting..." }
            return
        }

        getUserSettingsUseCase.invoke().ifSuccess { userSettings ->
            Napier.d(tag = "NotificationServiceManager") { "User settings loaded: $userSettings" }
            if (userSettings.data.receiveNotifications) {
                NotifierManager.getPushNotifier().subscribeToTopic(MAIN_TOPIC_NAME)
                Napier.d(tag = "NotificationServiceManager") { "Subscribed to topic: $MAIN_TOPIC_NAME" }
            } else {
                NotifierManager.getPushNotifier().unSubscribeFromTopic(MAIN_TOPIC_NAME)
                Napier.d(tag = "NotificationServiceManager") { "Unsubscribed from topic: $MAIN_TOPIC_NAME" }
            }
        }
    }

    companion object {
        const val MAIN_TOPIC_NAME = "main_topic"
    }
}