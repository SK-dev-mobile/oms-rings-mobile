package skdev.omsrings.mobile.presentation.base

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey

abstract class BaseScreen(private val screenName: String) : Screen {

    override val key: ScreenKey
        get() = screenName

    @Composable
    override fun Content() {
        MainContent()
    }

    @Composable
    abstract fun MainContent()
}
