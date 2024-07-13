package skdev.omsrings.mobile.utils.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalFocusManager


@Composable
fun HideKeyboardOnUpdate(
    updating: Boolean,
) {
    val focusManager = LocalFocusManager.current

    // Hide keyboard on updating
    LaunchedEffect(updating) {
        if (updating) {
            focusManager.clearFocus()
        }
    }
}