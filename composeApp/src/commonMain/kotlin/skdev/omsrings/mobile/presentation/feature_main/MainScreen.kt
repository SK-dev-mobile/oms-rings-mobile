package skdev.omsrings.mobile.presentation.feature_main

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.theme
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.presentation.base.BaseScreen

object MainScreen : BaseScreen("main_screen") {

    @Composable
    override fun MainContent() {
        val viewModel = viewModel { MainViewModel() }
        val t = stringResource(Res.string.theme)
    }
}
