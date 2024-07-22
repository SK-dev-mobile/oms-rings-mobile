package skdev.omsrings.mobile.presentation.feature_day_orders.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.collapse
import omsringsmobile.composeapp.generated.resources.full_history
import omsringsmobile.composeapp.generated.resources.history
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.presentation.feature_day_orders.enitity.OrderHistoryEvent
import skdev.omsrings.mobile.presentation.feature_day_orders.enitity.OrderHistoryEventType
import skdev.omsrings.mobile.ui.components.buttons.InlineIconButton
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens

@Composable
fun OrderHistory(
    modifier: Modifier = Modifier,
    history: List<OrderHistoryEvent>,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val lastEvent = history.lastOrNull()
    val createdEvent = history.find { it.type == OrderHistoryEventType.CREATED }
    val middleEvents = history.drop(1).dropLast(1)
    val eventsModifer = remember { Modifier.fillMaxWidth() }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
    ) {
        createdEvent?.let { event ->
            Text(
                text = stringResource(Res.string.history),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            EventItem(
                modifier = eventsModifer,
                event = event
            )
        }

        if (middleEvents.isNotEmpty() && !isExpanded) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (middleEvents.isNotEmpty()) {
            if (isExpanded) {
                for (event in middleEvents) {
                    key(event.time) {
                        EventItem(
                            modifier = eventsModifer,
                            event = event
                        )
                    }
                }
            }
        }

        if (lastEvent != createdEvent) {
            lastEvent?.let { event ->
                EventItem(
                    modifier = eventsModifer,
                    event = event
                )
            }
        }

        if (middleEvents.isNotEmpty()) {

            InlineIconButton(
                modifier = Modifier.fillMaxWidth(),
                text = if (isExpanded) stringResource(Res.string.collapse) else stringResource(Res.string.full_history),
                imageVector = if (isExpanded) Icons.Rounded.ArrowDropUp else Icons.Rounded.ArrowDropDown,
            ) {
                isExpanded = !isExpanded
            }
        }
    }
}

@Composable
fun EventItem(
    modifier: Modifier = Modifier,
    event: OrderHistoryEvent
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = event.time,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
        )

        Spacer(Dimens.spaceExtraSmall)

        Text(
            modifier = Modifier.width(128.dp),
            text = stringResource(event.type.resValue),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
        )

        Spacer(Dimens.spaceExtraSmall)

        Text(
            text = event.userFullName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
