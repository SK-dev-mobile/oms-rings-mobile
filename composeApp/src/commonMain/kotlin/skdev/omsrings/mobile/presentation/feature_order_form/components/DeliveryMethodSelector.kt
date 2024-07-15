package skdev.omsrings.mobile.presentation.feature_order_form.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.delivery
import omsringsmobile.composeapp.generated.resources.pickup
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.domain.model.DeliveryMethod
import skdev.omsrings.mobile.ui.theme.values.Dimens

@Composable
fun DeliveryMethodSelector(
    selectedMethod: DeliveryMethod,
    onMethodSelected: (DeliveryMethod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
    ) {
        DeliveryOption.entries.forEach { option ->
            DeliveryMethodOption(
                option = option,
                selected = selectedMethod == option.method,
                onSelected = { onMethodSelected(option.method) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeliveryMethodOption(
    option: DeliveryOption,
    selected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onSelected
    ) {
        Row(
            modifier = Modifier
                .padding(Dimens.spaceSmall)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = stringResource(option.label),
                tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(option.label),
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private enum class DeliveryOption(
    val method: DeliveryMethod,
    val label: StringResource,
    val icon: ImageVector
) {
    PICKUP(DeliveryMethod.PICKUP, Res.string.pickup, Icons.Rounded.DirectionsCar),
    DELIVERY(DeliveryMethod.DELIVERY, Res.string.delivery, Icons.Rounded.Home)
}