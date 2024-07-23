package skdev.omsrings.mobile.presentation.feature_day_orders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.datetime.LocalDate
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.create
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_day_orders.components.OrderView
import skdev.omsrings.mobile.presentation.feature_day_orders.enitity.OrderInfoModel
import skdev.omsrings.mobile.presentation.feature_faq.FAQScreen
import skdev.omsrings.mobile.presentation.feature_order_form.OrderFormScreen
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.AnimationSpec
import skdev.omsrings.mobile.ui.theme.values.Dimens
import skdev.omsrings.mobile.utils.datetime.DateTimePattern
import skdev.omsrings.mobile.utils.datetime.format
import skdev.omsrings.mobile.utils.flow.observeAsEffects

typealias OnAction = (DayOrdersScreenContract.Event) -> Unit

class DayOrdersScreen(
    private val selectedDate: LocalDate
) : BaseScreen("day_orders_screen") {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun MainContent() {
        val screenModel = koinScreenModel<DayOrdersScreenModel> { parametersOf(selectedDate) }
        val navigator = LocalNavigator.currentOrThrow
        val formattedSelectedDay = remember(selectedDate) {
            selectedDate.format(DateTimePattern.SIMPLE_DATE)
        }
        val dayOrders by screenModel.dayOrders.collectAsState()
        val updating by screenModel.updating.collectAsState()

        val lazyState = rememberLazyListState()
        val showFloatingButton by remember(lazyState) {
            derivedStateOf {
                lazyState.firstVisibleItemIndex == 0 && lazyState.firstVisibleItemScrollOffset < 40
            }
        }

        val refreshState = rememberPullToRefreshState()
        val uriHandler = LocalUriHandler.current

        LaunchedEffect(refreshState.isRefreshing) {
            if (refreshState.isRefreshing) {
                screenModel.onEvent(DayOrdersScreenContract.Event.OnStart)
                refreshState.endRefresh()
            }
        }

        LaunchedEffect(Unit) {
            screenModel.onEvent(DayOrdersScreenContract.Event.OnStart)
        }

        screenModel.effects.observeAsEffects {
            when (it) {
                DayOrdersScreenContract.Effect.NaivgateBack -> {
                    navigator.pop()
                }

                is DayOrdersScreenContract.Effect.NavigateToOrderDetails -> {
                    navigator.push(
                        OrderFormScreen(
                            selectedDate = it.selectedDate,
                            orderId = it.orderId
                        )
                    )
                }

                is DayOrdersScreenContract.Effect.NavigateToOrderForm -> {
                    navigator.push(
                        OrderFormScreen(
                            selectedDate = it.selectedDate,
                        )
                    )
                }

                is DayOrdersScreenContract.Effect.IntentCallAction -> {
                    uriHandler.openUri("tel:${it.phoneNumber}")
                }
            }
        }

        Scaffold(
            topBar = {
                RingsTopAppBar(
                    title = formattedSelectedDay,
                    onNavigationClicked = {
                        navigator.pop()
                    },
                    actions = {
                        AnimatedVisibility(
                            visible = !showFloatingButton,
                            enter = scaleIn(AnimationSpec.springHigh),
                            exit = fadeOut(AnimationSpec.tweenFast),
                        ) {
                            IconButton(
                                onClick = {
                                    screenModel.onEvent(DayOrdersScreenContract.Event.OnCreateOrderClicked)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AddCircleOutline,
                                    contentDescription = stringResource(Res.string.create)
                                )
                            }
                        }
                        IconButton(
                            onClick = {
                                // TODO: Add action
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Lock,
                                contentDescription = "Lock"
                            )
                        }
                        IconButton(
                            onClick = {
                                // TODO: Add action
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ShoppingCart,
                                contentDescription = "All"
                            )
                        }
                        IconButton(
                            onClick = {
                                navigator.push(FAQScreen)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Help,
                                contentDescription = "FAQ"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                AnimatedVisibility(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
                    visible = showFloatingButton,
                    enter = scaleIn(AnimationSpec.springHigh),
                    exit = fadeOut(AnimationSpec.tweenFast),
                ) {
                    ExtendedFloatingActionButton(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.primary,
                        onClick = {
                            screenModel.onEvent(DayOrdersScreenContract.Event.OnCreateOrderClicked)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddCircleOutline,
                            contentDescription = stringResource(Res.string.create)
                        )

                        Spacer(Dimens.spaceMedium)

                        Text(
                            text = stringResource(Res.string.create),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            },
            contentWindowInsets = WindowInsets.statusBars
        ) { paddingValues ->
            DayOrdersScreenContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                updating = updating,
                orders = dayOrders,
                lazyState = lazyState,
                refreshState = refreshState,
                onAction = screenModel::onEvent
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayOrdersScreenContent(
    modifier: Modifier = Modifier,
    updating: Boolean,
    lazyState: LazyListState,
    refreshState: PullToRefreshState,
    orders: List<OrderInfoModel>,
    onAction: OnAction,
) {
    Box(
        modifier = modifier.nestedScroll(refreshState.nestedScrollConnection)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyState,
            contentPadding = WindowInsets.navigationBars.asPaddingValues()
        ) {
            items(orders, key = { it.id }) {
                OrderView(
                    modifier = Modifier.fillMaxWidth(),
                    orderInfoModel = it,
                    onAction = onAction,
                )
            }
        }

        if (updating) {
            LinearProgressIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .height(4.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = refreshState
        )
    }
}
