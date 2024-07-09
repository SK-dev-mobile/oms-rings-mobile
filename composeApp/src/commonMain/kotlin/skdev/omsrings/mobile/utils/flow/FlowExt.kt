package skdev.omsrings.mobile.utils.flow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.collect

@Composable
fun <T> Flow<T>.observeAsEffects(onEach: (T) -> Unit) {
    LaunchedEffect(key1 = this) {
        this@observeAsEffects.onEach(onEach).collect()
    }
}
