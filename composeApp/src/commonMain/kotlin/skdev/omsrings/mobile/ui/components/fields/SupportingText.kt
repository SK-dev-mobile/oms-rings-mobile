package skdev.omsrings.mobile.ui.components.fields

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SupportingText(error: String?): @Composable (() -> Unit)? {
    return if (error == null) {
        null
    } else {
        {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun SupportingText(error: StringResource?): @Composable (() -> Unit)? {
    return if (error == null) {
        null
    } else {
        {
            Text(
                text = stringResource(error),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
