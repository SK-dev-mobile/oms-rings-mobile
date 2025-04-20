package skdev.omsrings.mobile.presentation.feature_profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.edit_content
import omsringsmobile.composeapp.generated.resources.user_logout
import omsringsmobile.composeapp.generated.resources.user_profile_title
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens

object ProfileScreen : BaseScreen("profile_screen") {
    @Composable
    override fun MainContent() {

        val localNavigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                RingsTopAppBar(
                    title = stringResource(Res.string.user_profile_title),
                    enabledNavigation = true,
                    onNavigationClicked = {
                        localNavigator.pop()
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(Dimens.spaceMedium)
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                TextButton(
                    onClick = { /* TODO logout */ }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Logout,
                        contentDescription = stringResource(Res.string.user_logout)
                    )

                    Spacer(Dimens.spaceMedium)

                    Text(
                        text = stringResource(Res.string.user_logout),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}