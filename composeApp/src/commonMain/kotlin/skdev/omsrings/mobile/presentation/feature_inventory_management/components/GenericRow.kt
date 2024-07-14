package skdev.omsrings.mobile.presentation.feature_inventory_management.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens

@Composable
fun GenericRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String? = null,
    onRowClick: (() -> Unit)? = null,
    primaryAction: (@Composable () -> Unit)? = null,
    secondaryAction: (@Composable () -> Unit)? = null,
    tertiaryAction: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceSmall)
            .clip(MaterialTheme.shapes.medium)
            .then(if (onRowClick != null) Modifier.clickable(onClick = onRowClick) else Modifier),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spaceMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint
            )
            Spacer(Dimens.spaceMedium)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            primaryAction?.invoke()
            secondaryAction?.invoke()
            tertiaryAction?.invoke()
        }
    }
}
