package skdev.omsrings.mobile.presentation.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

abstract class BaseViewModel<Effect, Event> : ViewModel() {
    // A state flow that represents the updating state of the ViewModel.
    private val _updating = MutableStateFlow(false)

    // A public read-only view of the updating state.
    val updating = _updating.asStateFlow()

    // A channel for effects that the view will handle.
    private val _effects: Channel<Effect> = Channel(Channel.BUFFERED)

    // A public read-only view of the effects channel as a flow.
    val effects: Flow<Effect> get() = _effects.receiveAsFlow()

    /**
     * Sets the updating state to true.
     * This should be called when the ViewModel starts updating its state.
     */
    fun onUpdateState() {
        _updating.value = true
    }

    /**
     * Sets the updating state to false.
     * This should be called when the ViewModel finishes updating its state.
     */
    fun onUpdatedState() {
        _updating.value = false
    }

    /**
     * Sends an effect to the effect channel.
     * This should be called when there is a new action that the view should handle.
     *
     * @param effect The effect to be handled.
     */
    suspend fun launchEffect(effect: Effect) {
        _effects.send(effect)
    }

    /**
     * Handle event from ui.
     * This should be called when there is a new action from view.
     *
     * @param event The event to be handled.
     */
    abstract fun onEvent(event: Event)
}
