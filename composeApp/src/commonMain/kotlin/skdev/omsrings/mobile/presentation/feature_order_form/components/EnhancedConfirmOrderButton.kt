package skdev.omsrings.mobile.presentation.feature_order_form.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.confirm_order
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens

@Composable
fun EnhancedConfirmOrderButton(
    onClick: () -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f)
    val backgroundColor by animateColorAsState(
        if (isEnabled) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant
    )

    Button(
        onClick = {
            scope.launch {
                isPressed = true
                delay(100)
                isPressed = false
                onClick()
            }
        },
        modifier = modifier
            .height(56.dp)
            .scale(scale),
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        contentPadding = PaddingValues(horizontal = Dimens.spaceMedium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = stringResource(Res.string.confirm_order)
            )
            Spacer(Dimens.spaceSmall)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(Res.string.confirm_order),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}