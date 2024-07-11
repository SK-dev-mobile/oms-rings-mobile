package skdev.omsrings.mobile.app

import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.koin.compose.koinInject
import skdev.omsrings.mobile.domain.model.InventoryItem
import skdev.omsrings.mobile.presentation.feature_auth.AuthScreen
import skdev.omsrings.mobile.presentation.feature_faq.FAQScreen
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementScreen
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

    Box {
        Navigator(
            InventoryManagementScreen
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
