package skdev.omsrings.mobile.presentation.feature_order_form.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun ConfirmOrderButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Text("Подтвердить заказ")
    }
}
