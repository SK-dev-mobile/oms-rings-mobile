package skdev.omsrings.mobile.presentation.feature_user_settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.user_settings_clear_orders
import omsringsmobile.composeapp.generated.resources.user_settings_clear_orders_description
import omsringsmobile.composeapp.generated.resources.user_settings_receive_notifications
import omsringsmobile.composeapp.generated.resources.user_settings_show_cleared_orders
import omsringsmobile.composeapp.generated.resources.user_settings_title
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_user_settings.UserSettingsContract.Event
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar

object UserSettingsScreen : BaseScreen("user_settings_screen") {


    @Composable
    override fun MainContent() {
        val screenModel = koinScreenModel<UserSettingsModel>()
        val state by screenModel.state.collectAsState()

        Scaffold(
            topBar = {
                RingsTopAppBar(
                    title = stringResource(Res.string.user_settings_title),
                    onNavigationClicked = { /* TODO */ },
                    actions = {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            modifier = Modifier.padding(end = 16.dp),
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
                    .padding(16.dp)
            ) {
                SettingItem(
                    title = stringResource(Res.string.user_settings_receive_notifications),
                    checked = state.receiveNotifications,
                    onCheckedChange = { screenModel.onEvent(Event.ToggleNotifications) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )

                SettingItem(
                    title = stringResource(Res.string.user_settings_show_cleared_orders),
                    checked = state.showClearedOrders,
                    onCheckedChange = { screenModel.onEvent(Event.ToggleShowClearedOrders) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

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
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.user_settings_clear_orders_description),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onClearClicked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(Res.string.user_settings_clear_orders))
                }
            }
        }
    }

}