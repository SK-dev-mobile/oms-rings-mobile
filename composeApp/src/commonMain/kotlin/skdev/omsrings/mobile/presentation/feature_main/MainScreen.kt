package skdev.omsrings.mobile.presentation.feature_main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.aakira.napier.Napier
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.app_name
import omsringsmobile.composeapp.generated.resources.edit_content
import omsringsmobile.composeapp.generated.resources.user_profile_title
import omsringsmobile.composeapp.generated.resources.user_settings_title
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_main.components.CalendarView
import skdev.omsrings.mobile.presentation.feature_main.components.rememberCalendarState
import skdev.omsrings.mobile.presentation.feature_user_settings.UserSettingsScreen
import skdev.omsrings.mobile.ui.components.fields.PhoneField
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens

object MainScreen : BaseScreen("main_screen") {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun MainContent() {
        val screenModel = koinScreenModel<MainScreenModel>()
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                RingsTopAppBar(
                    title = stringResource(Res.string.app_name),
                    enabledNavigation = false,
                    actions = {
                        IconButton(
                            onClick = { /* TODO */ }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = stringResource(Res.string.user_profile_title)
                            )
                        }
                        IconButton(
                            onClick = { navigator.push(UserSettingsScreen) }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = stringResource(Res.string.user_settings_title)
                            )
                        }
                    },
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { /* TODO */ }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = stringResource(Res.string.edit_content)
                    )

                    Spacer(Dimens.spaceMedium)

                    Text(
                        text = stringResource(Res.string.edit_content),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        ) { paddingValues ->
            MainScreenContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }

    @Composable
    private fun MainScreenContent(
        modifier: Modifier = Modifier
    ) {
        val calendarState = rememberCalendarState()

        Column(
            modifier = modifier
        ) {
            CalendarView(
                modifier = Modifier.fillMaxWidth(),
                state = calendarState,
            )
        }
    }
}
