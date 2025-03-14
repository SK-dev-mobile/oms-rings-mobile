package skdev.omsrings.mobile.app

import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.mmk.kmpnotifier.notification.NotifierManager
import org.koin.compose.koinInject
import skdev.omsrings.mobile.presentation.feature_main.MainScreen
import skdev.omsrings.mobile.ui.components.notification.NotificationDisplay
import skdev.omsrings.mobile.ui.theme.AppTheme
import skdev.omsrings.mobile.utils.notification.NotificationManager

@OptIn(ExperimentalVoyagerApi::class)
@Composable
internal fun App() = AppTheme(
    isDark = isSystemInDarkTheme()
) {
    val notificationManager: NotificationManager = koinInject()

    NotifierManager.addListener(object : NotifierManager.Listener {
        override fun onNewToken(token: String) {
            println("onNewToken: $token")
        }
    })

    Box {
        Navigator(
            MainScreen
        ) { navigator ->
            SlideTransition(
                navigator = navigator,
                animationSpec = tween(250)
            )
        }
        NotificationDisplay(
            notificationManager = notificationManager,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing),
        )
    }
}
