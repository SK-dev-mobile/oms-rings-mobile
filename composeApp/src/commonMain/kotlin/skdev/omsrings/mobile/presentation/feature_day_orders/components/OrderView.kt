package skdev.omsrings.mobile.presentation.feature_day_orders.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import skdev.omsrings.mobile.presentation.feature_day_orders.DayOrdersScreenContract
import skdev.omsrings.mobile.presentation.feature_day_orders.OnAction
import skdev.omsrings.mobile.presentation.feature_day_orders.enitity.OrderInfoModel
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.AnimationSpec
import skdev.omsrings.mobile.ui.theme.values.Dimens


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrderView(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    orderInfoModel: OrderInfoModel,
    onAction: OnAction,
) {

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 2 },
    )

    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .clickable(enabled) {
                onAction(DayOrdersScreenContract.Event.OnOrderDetailsClicked(orderInfoModel.id))
            }
            .animateContentSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        OrderHeader(
            modifier = Modifier
                .padding(Dimens.spaceMedium)
                .fillMaxWidth(),
            enabled = enabled,
            orderId = orderInfoModel.id,
            createdBy = orderInfoModel.createdBy,
            contactPhone = orderInfoModel.contactPhone,
            onCallClicked = {
                if (orderInfoModel.contactPhone != null) {
                    onAction(DayOrdersScreenContract.Event.OnCallClicked(orderInfoModel.contactPhone))
                }
            }
        )

        HorizontalPager(
            modifier = Modifier
                .animateContentSize()
                .fillMaxWidth(),
            state = pagerState,
            verticalAlignment = Alignment.Top,
            pageSpacing = Dimens.spaceMedium,
            contentPadding = PaddingValues(horizontal = Dimens.spaceMedium)
        ) { page ->
            if (page == 0) {
                OrderMainInfo(
                    modifier = Modifier.fillMaxWidth(),
                    comment = orderInfoModel.comment,
                    address = orderInfoModel.address,
                    isDelivery = orderInfoModel.isDelivery,
                    pickupTime = orderInfoModel.pickupTime,
                    onOrderItemsClicked = {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                1,
                                animationSpec = AnimationSpec.tweeSlow
                            )
                        }
                    }
                )
            } else {
                OrderItems(
                    modifier = Modifier.fillMaxWidth(),
                    items = orderInfoModel.items
                )
            }
        }

        Spacer(Dimens.spaceMedium)

        OrderHistory(
            modifier = Modifier
                .padding(horizontal = Dimens.spaceMedium)
                .fillMaxWidth(),
            history = orderInfoModel.history
        )

        Spacer(Dimens.spaceMedium)

        OrderStatus(
            modifier = Modifier
                .padding(horizontal = Dimens.spaceMedium)
                .fillMaxWidth(),
            enabled = enabled,
            status = orderInfoModel.status,
            onChangeStatusClicked = { status ->
                onAction(
                    DayOrdersScreenContract.Event.OnUpdateOrderStatusClicked(
                        orderInfoModel.id,
                        status
                    )
                )
            }
        )

        Spacer(Dimens.spaceMedium)

        HorizontalDivider()
    }
}
