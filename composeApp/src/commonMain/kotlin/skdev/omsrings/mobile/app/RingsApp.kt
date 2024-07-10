package skdev.omsrings.mobile.app

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.jetpack.ProvideNavigatorLifecycleKMPSupport
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import skdev.omsrings.mobile.presentation.feature_main.MainScreen
import skdev.omsrings.mobile.ui.components.error.ErrorPresenter
import skdev.omsrings.mobile.ui.theme.AppTheme
import skdev.omsrings.mobile.ui.theme.values.AnimationSpec

@OptIn(ExperimentalVoyagerApi::class)
@Composable
internal fun RingsApp() = AppTheme {

    // TODO: impl
//    val errorPresenter: ErrorPresenter = koinInject()

    Scaffold {
        ProvideNavigatorLifecycleKMPSupport {
            Navigator(
                MainScreen
            ) { navigator ->
                SlideTransition(
                    navigator = navigator,
                    animationSpec = tween(250)
                )
            }
        }
    }
}
