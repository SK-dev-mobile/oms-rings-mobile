package skdev.omsrings.mobile.presentation.feature_day_orders.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.item_quantity
import omsringsmobile.composeapp.generated.resources.order_composition
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.presentation.feature_day_orders.enitity.OrderItem
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens


@Composable
fun OrderItems(
    modifier: Modifier = Modifier,
    items: List<OrderItem>
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceExtraSmall, Alignment.Top),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(Res.string.order_composition),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )

        for(item in items) {
            key(item.inventoryName) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = item.inventoryName,
                        softWrap = true
                    )
                    Text(
                        text = stringResource(Res.string.item_quantity, item.quantity)
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.height(1.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}