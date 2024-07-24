package skdev.omsrings.mobile.presentation.feature_main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.datetime.LocalDate
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.app_name
import omsringsmobile.composeapp.generated.resources.edit_content
import omsringsmobile.composeapp.generated.resources.user_profile_title
import omsringsmobile.composeapp.generated.resources.user_settings_title
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.domain.model.DayInfoModel
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_day_orders.DayOrdersScreen
import skdev.omsrings.mobile.presentation.feature_main.components.CalendarState
import skdev.omsrings.mobile.presentation.feature_main.components.CalendarView
import skdev.omsrings.mobile.presentation.feature_main.components.rememberCalendarState
import skdev.omsrings.mobile.presentation.feature_user_settings.UserSettingsScreen
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens

private typealias OnAction = (MainScreenContract.Event) -> Unit

object MainScreen : BaseScreen("main_screen") {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun MainContent() {
        val screenModel = koinScreenModel<MainScreenModel>()
        val navigator = LocalNavigator.currentOrThrow
        val uiState by screenModel.uiState.collectAsState()
        val updating by screenModel.updating.collectAsState()
        val calendarState = rememberCalendarState {
            screenModel.onEvent(MainScreenContract.Event.OnLoadMonthInfo(it))
        }

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
                    onClick = {
                        navigator.push(
                            DayOrdersScreen(selectedDate = calendarState.selectedDate.value)
                        )
                    }
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
                    .padding(paddingValues),
                calendarState = calendarState,
                calendarDays = uiState.calendarDays,
                updating = updating,
            )
        }
    }

    @Composable
    private fun MainScreenContent(
        modifier: Modifier = Modifier,
        calendarState: CalendarState,
        calendarDays: Map<LocalDate, DayInfoModel>,
        updating: Boolean,
    ) {
        Column(
            modifier = modifier
        ) {
            CalendarView(
                modifier = Modifier.fillMaxWidth(),
                updating = updating,
                state = calendarState,
                calendarDays = calendarDays
            )
        }
    }
}
