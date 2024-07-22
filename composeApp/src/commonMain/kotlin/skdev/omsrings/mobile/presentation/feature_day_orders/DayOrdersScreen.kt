package skdev.omsrings.mobile.presentation.feature_day_orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.datetime.LocalDate
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_day_orders.components.OrderView
import skdev.omsrings.mobile.presentation.feature_day_orders.enitity.OrderInfoModel
import skdev.omsrings.mobile.presentation.feature_day_orders.enitity.generateSampleOrderInfoList
import skdev.omsrings.mobile.presentation.feature_faq.FAQScreen
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar
import skdev.omsrings.mobile.ui.theme.values.Dimens
import skdev.omsrings.mobile.utils.datetime.DateTimePattern
import skdev.omsrings.mobile.utils.datetime.format

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

        Scaffold(
            topBar = {
                RingsTopAppBar(
                    title = formattedSelectedDay,
                    onNavigationClicked = {
                        navigator.pop()
                    },
                    actions = {
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
            contentWindowInsets = WindowInsets.statusBars
        ) { paddingValues ->
            DayOrdersScreenContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                orders = remember {
                    generateSampleOrderInfoList()
                },
                onAction = {

                }
            )
        }
    }
}

@Composable
fun DayOrdersScreenContent(
    modifier: Modifier = Modifier,
    orders: List<OrderInfoModel>,
    onAction: OnAction,
) {
    val lazyState = rememberLazyListState()
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
