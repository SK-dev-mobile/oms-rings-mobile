package skdev.omsrings.mobile.presentation.feature_day_orders.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.order_number
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.domain.model.UUID
import skdev.omsrings.mobile.presentation.feature_day_orders.DayOrdersScreenContract
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens
import skdev.omsrings.mobile.ui.theme.values.IconSize


@Composable
fun OrderHeader(
    modifier: Modifier = Modifier,
    orderId: UUID,
    enabled: Boolean,
    createdBy: String,
    contactPhone: String?,
    onCallClicked: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .padding(end = Dimens.spaceMedium)
                .weight(.5f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = createdBy,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = stringResource(Res.string.order_number, orderId),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (contactPhone != null) {
            Button(
                enabled = enabled,
                shape = MaterialTheme.shapes.large,
                onClick = onCallClicked,
                contentPadding = PaddingValues(horizontal = Dimens.spaceSmall)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(IconSize.Small),
                        imageVector = Icons.Rounded.Phone,
                        contentDescription = ""
                    )
                    Spacer(Dimens.spaceExtraSmall)
                    Text(
                        text = contactPhone,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
