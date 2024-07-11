package skdev.omsrings.mobile.presentation.feature_auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.koinScreenModel
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.back
import omsringsmobile.composeapp.generated.resources.theme
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar

private typealias OnAction = (AuthScreenContract.Event) -> Unit

object AuthScreen : BaseScreen("auth_screen") {

    @Composable
    override fun MainContent() {
        val screenModel = koinScreenModel<AuthScreenModel>()

        Scaffold(
            topBar = {
                RingsTopAppBar(
                    title = stringResource(Res.string.back),
                    onNavigationClicked = {

                    }
                )
            }
        ) { paddingValues ->
            AuthScreenContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                onAction = screenModel::onEvent
            )
        }
    }

    @Composable
    private fun AuthScreenContent(
        modifier: Modifier = Modifier,
        onAction: OnAction,
    ) {
        Column(
            modifier = modifier
        ) {
            Button(
                onClick = { onAction(AuthScreenContract.Event.OnSignUpClicked) }
            ) {
                Text ("Зарегистрироваться")
            }
        }
    }

}
