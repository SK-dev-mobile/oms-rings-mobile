package skdev.omsrings.mobile.app

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import skdev.omsrings.mobile.ui.components.error.ErrorPresenter
import skdev.omsrings.mobile.ui.theme.AppTheme

@Composable
internal fun RingsApp() = AppTheme {

    // TODO: impl
//    val errorPresenter: ErrorPresenter = koinInject()

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
//                .background(MaterialTheme.colorScheme.backgroundGradient)
//                .safeDrawingPadding()
        ) {
//            Navigator(
//                LaunchScreen()
//            ) { navigator ->
//                SlideTransition(
//                    navigator = navigator,
//                    animationSpec = tween(250)
//                )
//            }
        }
    }
}
