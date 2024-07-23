package skdev.omsrings.mobile.presentation.feature_day_orders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.fromMilliseconds
import kotlinx.datetime.LocalDate
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.create
import omsringsmobile.composeapp.generated.resources.edit_content
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.domain.usecase.feature_order.CreateOrderUseCase
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_day_orders.components.OrderView
import skdev.omsrings.mobile.presentation.feature_day_orders.enitity.OrderInfoModel
import skdev.omsrings.mobile.presentation.feature_day_orders.enitity.generateSampleOrderInfoList
import skdev.omsrings.mobile.presentation.feature_faq.FAQScreen
import skdev.omsrings.mobile.presentation.feature_order_form.OrderFormScreen
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.AnimationSpec
import skdev.omsrings.mobile.ui.theme.values.Dimens
import skdev.omsrings.mobile.utils.datetime.DateTimePattern
import skdev.omsrings.mobile.utils.datetime.format
import skdev.omsrings.mobile.utils.datetime.toInstant

typealias OnAction = (DayOrdersScreenContract.Event) -> Unit

class DayOrdersScreen(
    private val selectedDay: LocalDate
) : BaseScreen("day_orders_screen") {

    @Composable
    override fun MainContent() {

        val navigator = LocalNavigator.currentOrThrow
        val formattedSelectedDay = remember(selectedDay) {
            selectedDay.format(DateTimePattern.SIMPLE_DATE)
        }

        val lazyState = rememberLazyListState()
        val showFloatingButton by remember(lazyState) {
            derivedStateOf {
                lazyState.firstVisibleItemIndex == 0 && lazyState.firstVisibleItemScrollOffset < 40
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
                                    navigator.push(
                                        OrderFormScreen(
                                            selectedDate = selectedDay
                                        )
                                    )
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
                            navigator.push(
                                OrderFormScreen(
                                    selectedDate = selectedDay
                                )
                            )
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
                orders = remember {
                    generateSampleOrderInfoList()
                },
                lazyState = lazyState,
                onAction = {

                }
            )
        }
    }
}

@Composable
fun DayOrdersScreenContent(
    modifier: Modifier = Modifier,
    lazyState: LazyListState,
    orders: List<OrderInfoModel>,
    onAction: OnAction,
) {
    LazyColumn(
        modifier = modifier,
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
}
