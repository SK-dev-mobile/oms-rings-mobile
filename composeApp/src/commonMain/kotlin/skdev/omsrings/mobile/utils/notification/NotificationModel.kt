package skdev.omsrings.mobile.utils.notification

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource

sealed interface NotificationModel {
    data class Error(
        val titleRes: StringResource,
        val messageRes: StringResource,
        val icon: ImageVector = Icons.Default.Warning
    ) : NotificationModel

    data class Toast(
        val titleRes: StringResource,
        val messageRes: StringResource,
        val icon: ImageVector,
        val type: ToastType = ToastType.Info
    ) : NotificationModel

    data class Custom(
        val content: @Composable (modifier: Modifier) -> Unit
    ) : NotificationModel
}

enum class ToastType {
    Success,
    Warning,
    Info
}
