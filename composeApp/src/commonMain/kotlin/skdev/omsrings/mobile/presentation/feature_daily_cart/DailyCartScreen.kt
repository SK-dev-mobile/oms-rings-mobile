package skdev.omsrings.mobile.presentation.feature_daily_cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.datetime.LocalDate
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.total_amount_for_date
import omsringsmobile.composeapp.generated.resources.total_order
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import skdev.omsrings.mobile.domain.model.FolderWithItems
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens
import skdev.omsrings.mobile.utils.flow.observeAsEffects

class DailyCartScreen(
    private val selectedDate: LocalDate
) : BaseScreen("daily_cart_screen") {

    @Composable
    override fun MainContent() {
        val screenModel = koinScreenModel<DailyCartScreenModel> { parametersOf(selectedDate) }
        val state by screenModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        screenModel.effects.observeAsEffects {
            when(it) {
                DailyCartContract.Effect.NavigateBack -> {
                    navigator.pop()
                }
            }
        }

        Scaffold(
            topBar = {
                RingsTopAppBar(
                    title = stringResource(Res.string.total_order),
                    onNavigationClicked = { screenModel.onEvent(DailyCartContract.Event.OnBackClicked) }
                )
            },
            contentWindowInsets = WindowInsets.statusBars
        ) { paddingValues ->
            DailyCartScreenContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                state = state,
                onRefresh = {
                    screenModel.onEvent(DailyCartContract.Event.OnRefresh)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DailyCartScreenContent(
    modifier: Modifier = Modifier,
    state: DailyCartContract.State,
    onRefresh: () -> Unit
) {
    val lazyState = rememberLazyListState()
    val refreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        modifier = modifier,
        state = refreshState,
        isRefreshing = state.isLoading,
        onRefresh = onRefresh
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyState,
            contentPadding = WindowInsets.navigationBars.asPaddingValues()
        ) {
            item {
                Text(
                    text = stringResource(Res.string.total_amount_for_date, state.selectedDate),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(Dimens.spaceMedium)
                )
            }

            items(state.folders) { folderWithItems ->
                FolderSection(folderWithItems)
                Spacer(Dimens.spaceMedium)
            }

            item {
                Spacer(Dimens.spaceLarge)
            }
        }

        if (state.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .height(4.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
private fun FolderSection(folderWithItems: FolderWithItems) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spaceMedium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = folderWithItems.folder.name,
                style = MaterialTheme.typography.titleSmall
            )
            HorizontalDivider(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Dimens.spaceSmall)
                    .align(Alignment.CenterVertically)
            )
        }

        Spacer(Dimens.spaceSmall)

        folderWithItems.items.forEach { itemWithQuantity ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.spaceSmall),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = itemWithQuantity.item.name,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${itemWithQuantity.quantity} шт.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
} 