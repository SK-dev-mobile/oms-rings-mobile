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
import org.koin.compose.koinInject
import skdev.omsrings.mobile.domain.repository.AuthRepository
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_auth.AuthScreen
import skdev.omsrings.mobile.presentation.feature_main.MainScreen
import skdev.omsrings.mobile.ui.components.notification.NotificationDisplay
import skdev.omsrings.mobile.ui.theme.AppTheme
import skdev.omsrings.mobile.utils.notification.NotificationManager

@Composable
internal fun App() = AppTheme(
    isDark = isSystemInDarkTheme()
) {
    val notificationManager: NotificationManager = koinInject()
    val authRepository = koinInject<AuthRepository>()
    var userAuthState by remember { mutableStateOf(UserAuthState.None) }
    val notificationServiceManager = koinInject<NotificationServiceManager>()  // Оставить для init

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
