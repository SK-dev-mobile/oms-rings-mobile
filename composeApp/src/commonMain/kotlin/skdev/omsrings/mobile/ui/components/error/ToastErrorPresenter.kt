package skdev.omsrings.mobile.ui.components.error

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

@Stable
class ToastErrorPresenter(
    private val duration: Long = PresentDuration.LONG
) : ErrorPresenter() {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override operator fun invoke() {
        // TODO: Impl presentation toast
    }

    companion object {
        object PresentDuration {
            const val LONG = 3_000L
        }
    }
}