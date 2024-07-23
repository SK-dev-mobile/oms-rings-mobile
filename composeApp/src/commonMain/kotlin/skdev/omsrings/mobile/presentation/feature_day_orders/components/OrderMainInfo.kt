package skdev.omsrings.mobile.presentation.feature_day_orders.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.comment
import omsringsmobile.composeapp.generated.resources.delivery
import omsringsmobile.composeapp.generated.resources.delivery_address
import omsringsmobile.composeapp.generated.resources.order_composition
import omsringsmobile.composeapp.generated.resources.order_general_information
import omsringsmobile.composeapp.generated.resources.pickup
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.ui.components.buttons.InlineIconButton
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens
import skdev.omsrings.mobile.ui.theme.values.IconSize

@Composable
fun OrderMainInfo(
    modifier: Modifier = Modifier,
    comment: String?,
    address: String?,
    isDelivery: Boolean,
    pickupTime: String,
    onOrderItemsClicked: () -> Unit,
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(Res.string.order_general_information),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            InlineIconButton(
                modifier = Modifier,
                text = stringResource(Res.string.order_composition),
                tint = MaterialTheme.colorScheme.surfaceVariant,
                imageVector = Icons.Rounded.ChevronRight,
                onClick = onOrderItemsClicked
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            InfoElementRow(
                modifier = Modifier
                    .weight(.5f),
                description = stringResource(
                    if (isDelivery) Res.string.delivery else Res.string.pickup
                ),
                value = pickupTime,
                icon = Icons.Rounded.Schedule
            )

            if (address != null  && address.isNotBlank()) {
                InfoElementRow(
                    modifier = Modifier
                        .weight(.5f),
                    description = stringResource(Res.string.delivery_address),
                    value = address,
                    icon = Icons.Rounded.LocationOn
                )
            }
        }


        if (comment != null) {

            InfoElementRow(
                modifier = Modifier.fillMaxWidth(),
                description = stringResource(Res.string.comment),
                value = comment,
                icon = Icons.AutoMirrored.Rounded.Chat
            )
        }
    }
}


@Composable
private fun InfoElementRow(
    modifier: Modifier = Modifier,
    description: String,
    icon: ImageVector,
    value: String,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
    ) {
        VerticalDivider(
            modifier = Modifier
                .height(28.dp)
                .width(1.dp),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Dimens.spaceSmall)

        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(IconSize.Small),
                    imageVector = icon,
                    contentDescription = description
                )

                Spacer(Dimens.spaceSmall)

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Dimens.spaceExtraSmall)

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = value,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
