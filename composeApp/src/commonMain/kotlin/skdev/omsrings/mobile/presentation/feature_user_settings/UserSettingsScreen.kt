package skdev.omsrings.mobile.presentation.feature_user_settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import omsringsmobile.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_user_settings.UserSettingsContract.Event
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens

object UserSettingsScreen : BaseScreen("user_settings_screen") {


    @Composable
    override fun MainContent() {
        val screenModel = koinScreenModel<UserSettingsScreenModel>()
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                RingsTopAppBar(
                    title = stringResource(Res.string.user_settings_title),
                    onNavigationClicked = {
                        navigator.pop()
                    },
                    actions = {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            modifier = Modifier.padding(end = Dimens.spaceMedium),
                            contentDescription = stringResource(Res.string.user_settings_title)
                        )
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(Dimens.spaceMedium)
            ) {
//                SettingItem(
//                    title = stringResource(Res.string.user_settings_receive_notifications),
//                    checked = state.receiveNotifications,
//                    onCheckedChange = { screenModel.onEvent(Event.ToggleNotifications) },
//                    modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.spaceSmall)
//                )

                SettingItem(
                    title = stringResource(Res.string.user_settings_show_cleared_orders),
                    checked = state.showClearedOrders,
                    onCheckedChange = { screenModel.onEvent(Event.ToggleShowClearedOrders) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.spaceSmall)
                )

                Spacer(Dimens.spaceLarge)

                ClearOrdersCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClearClicked = { screenModel.onEvent(Event.ClearOrders) }
                )
            }
        }

    }

    @Composable
    private fun SettingItem(
        title: String,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }

    @Composable
    private fun ClearOrdersCard(
        modifier: Modifier = Modifier,
        onClearClicked: () -> Unit
    ) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(Dimens.spaceMedium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Dimens.spaceSmall)
                Text(
                    text = stringResource(Res.string.user_settings_clear_orders_description),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(Dimens.spaceMedium)
                Button(
                    onClick = onClearClicked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.spaceMedium)
                    )
                    Spacer(Dimens.spaceSmall)
                    Text(stringResource(Res.string.user_settings_clear_orders))
                }
            }
        }
    }

}