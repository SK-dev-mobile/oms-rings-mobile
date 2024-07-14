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
import androidx.compose.ui.unit.dp
import skdev.omsrings.mobile.domain.model.DeliveryMethod

@Composable
fun DeliveryMethodSelector(
    selectedMethod: DeliveryMethod,
    onMethodSelected: (DeliveryMethod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .selectableGroup()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = option.label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private enum class DeliveryOption(
    val method: DeliveryMethod,
    val label: String,
    val icon: ImageVector
) {
    PICKUP(DeliveryMethod.PICKUP, "Самовывоз", Icons.Rounded.DirectionsCar),
    DELIVERY(DeliveryMethod.DELIVERY, "Доставка", Icons.Rounded.Home)
}