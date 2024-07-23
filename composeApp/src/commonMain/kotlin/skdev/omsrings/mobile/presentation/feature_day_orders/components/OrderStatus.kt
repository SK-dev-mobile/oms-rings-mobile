package skdev.omsrings.mobile.presentation.feature_day_orders.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.domain.model.OrderStatus
import skdev.omsrings.mobile.presentation.feature_day_orders.enitity.toResValue
import skdev.omsrings.mobile.ui.theme.CustomTheme
import skdev.omsrings.mobile.ui.theme.values.Dimens


@Composable
fun OrderStatus(
    modifier: Modifier = Modifier,
    status: OrderStatus,
    onChangeStatusClicked: (status: OrderStatus) -> Unit,
) {
    Row(
       modifier = modifier
           .fillMaxWidth()
           .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            shape = MaterialTheme.shapes.large,
            color = when (status) {
                OrderStatus.CREATED -> CustomTheme.colors.successContainer
                OrderStatus.COMPLETED -> CustomTheme.colors.warningContainer
                OrderStatus.ARCHIVED -> CustomTheme.colors.errorContainer
            },
            contentColor = when (status) {
                OrderStatus.CREATED -> CustomTheme.colors.onSuccessContainer
                OrderStatus.COMPLETED -> CustomTheme.colors.onWarningContainer
                OrderStatus.ARCHIVED -> CustomTheme.colors.onErrorContainer
            }
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocalShipping,
                    contentDescription = null
                )

                Text(
                    text = stringResource(status.toResValue())
                )
            }
        }
        AnimatedVisibility(
            visible = status != OrderStatus.ARCHIVED
        ) {
            Button(
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxHeight(),
                onClick = {
                    onChangeStatusClicked(
                        when(status) {
                            OrderStatus.CREATED -> OrderStatus.COMPLETED
                            OrderStatus.COMPLETED -> OrderStatus.ARCHIVED
                            else -> OrderStatus.CREATED
                        }
                    )
                },
                contentPadding = PaddingValues(Dimens.spaceSmall),
                shape = MaterialTheme.shapes.large,
            ) {
                Icon(
                    imageVector = remember(status) {
                        when(status) {
                            OrderStatus.CREATED -> Icons.Rounded.Check
                            else -> Icons.Rounded.Archive
                        }
                    },
                    contentDescription = null
                )
            }
        }
    }
}
