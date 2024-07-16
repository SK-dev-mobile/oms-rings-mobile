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
import dev.gitlive.firebase.firestore.Timestamp
import org.koin.compose.koinInject
import skdev.omsrings.mobile.presentation.feature_order_form.OrderFormScreen
import skdev.omsrings.mobile.ui.components.notification.NotificationDisplay
import skdev.omsrings.mobile.ui.theme.AppTheme
import skdev.omsrings.mobile.utils.notification.NotificationManager

@OptIn(ExperimentalVoyagerApi::class)
@Composable
internal fun App() = AppTheme(
    isDark = isSystemInDarkTheme()
) {
    val notificationManager: NotificationManager = koinInject()

    Box {
        Navigator(
            OrderFormScreen(selectedDate = Timestamp.now(), orderId = "af9e24d9-6a62-4a83-9067-59bf82a1bb35"),
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
