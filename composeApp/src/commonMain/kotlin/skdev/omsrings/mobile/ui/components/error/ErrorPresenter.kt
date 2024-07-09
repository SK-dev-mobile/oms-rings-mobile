package skdev.omsrings.mobile.ui.components.error

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import skdev.omsrings.mobile.utils.error.ErrorMessage
import skdev.omsrings.mobile.utils.error.ErrorObserver

@Stable
abstract class ErrorPresenter : ErrorObserver {
    private val _error = Channel<ErrorMessage?>(Channel.BUFFERED)
    protected val error = _error.receiveAsFlow()

    @Composable
    open operator fun invoke() {

    }

    override fun onError(error: ErrorMessage) {
//        Log.d("ErrorPresenter", "onError: $error") TODO: impl logging
        _error.trySend(error)
    }
}