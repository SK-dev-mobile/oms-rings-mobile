package skdev.omsrings.mobile.app

import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.mmk.kmpnotifier.notification.NotifierManager
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import skdev.omsrings.mobile.domain.repository.AuthRepository
import skdev.omsrings.mobile.domain.usecase.feature_auth.IsAuthorizedUseCase
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_auth.AuthScreen
import skdev.omsrings.mobile.presentation.feature_main.MainScreen
import skdev.omsrings.mobile.ui.components.notification.NotificationDisplay
import skdev.omsrings.mobile.ui.theme.AppTheme
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.omsrings.mobile.utils.notification.PushManager
import skdev.omsrings.mobile.utils.result.ifError
import skdev.omsrings.mobile.utils.result.ifSuccess

@Composable
internal fun App() = AppTheme(
    isDark = isSystemInDarkTheme()
) {
    LaunchedEffect(Unit) {
        PushManager.sendPush(
            "Приложение было запущено",
            "Приложение было запущено на каком-то устройстве."
        )
    }

    val notificationManager: NotificationManager = koinInject()
    val isAuthorizedUseCase: IsAuthorizedUseCase = koinInject()
    val authRepository = koinInject<AuthRepository>()
    var isTokenReady by remember { mutableStateOf(false) }

    NotifierManager.addListener(object : NotifierManager.Listener {
        override fun onNewToken(token: String) {
            println("onNewToken: $token")
            isTokenReady = true
        }

        override fun onPushNotification(title: String?, body: String?) {
            super.onPushNotification(title, body)
            println("Push: $title, $body")
        }
    })

    LaunchedEffect(isTokenReady) {
        if (isTokenReady) {
            println("Token is ready, start subscribe to topic")
            delay(3000)
            NotifierManager.getPushNotifier().subscribeToTopic("main_topic")
            println("Subscribe")
        }
    }

    var userAuthState by remember { mutableStateOf(UserAuthState.Unauthorized) }

    LaunchedEffect(Unit) {
        authRepository.authorizedFlow.collect() { isAuthorized ->
            userAuthState = if (isAuthorized) {
                UserAuthState.Authorized
            } else {
                UserAuthState.Unauthorized
            }
        }
    }


    Box {
        when (userAuthState) {
            UserAuthState.Authorized -> {
                Navigator(
                    MainScreen
                ) { navigator ->
                    SlideTransition(
                        navigator = navigator,
                        animationSpec = tween(250)
                    )
                }
            }
            UserAuthState.Unauthorized -> {
                Navigator(
                    AuthScreen
                ) { navigator ->
                    SlideTransition(
                        navigator = navigator,
                        animationSpec = tween(250)
                    )
                }
            }
            UserAuthState.None -> BankScreen
        }


        NotificationDisplay(
            notificationManager = notificationManager,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing),
        )
    }
}

private object BankScreen : BaseScreen("bank_screen") {
    @Composable
    override fun MainContent() {

    }
}


private enum class UserAuthState {
    Authorized,
    Unauthorized,
    None
}
