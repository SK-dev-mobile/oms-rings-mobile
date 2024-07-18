package skdev.omsrings.mobile.presentation.feature_day_orders

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.datetime.LocalDate
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_faq.FAQScreen
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar
import skdev.omsrings.mobile.utils.datetime.DateTimePattern
import skdev.omsrings.mobile.utils.datetime.format

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
            }
        ) {

        }
    }
}

