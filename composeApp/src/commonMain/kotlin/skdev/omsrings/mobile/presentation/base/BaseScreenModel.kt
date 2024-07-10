package skdev.omsrings.mobile.presentation.base

import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import org.jetbrains.compose.resources.StringResource
import skdev.omsrings.mobile.utils.error.Error
import skdev.omsrings.mobile.utils.error.toNotificationModel
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.omsrings.mobile.utils.notification.NotificationModel
import skdev.omsrings.mobile.utils.notification.ToastType

abstract class BaseScreenModel<Effect, Event>(
    private val notificationManager: NotificationManager
) : ScreenModel {
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

    /**
     * Show toast notification.
     *
     * @param titleRes The title of the toast.
     * @param messageRes The message of the toast.
     * @param icon The icon of the toast.
     * @param type The type of the toast, see [ToastType].
     */
    fun showToast(
        titleRes: StringResource,
        messageRes: StringResource,
        icon: ImageVector,
        type: ToastType = ToastType.Info
    ) {
        notificationManager.show(NotificationModel.Toast(titleRes, messageRes, icon, type))
    }

    /**
     * Show error notification.
     *
     * @param error The error to be shown, see [toNotificationModel].
     */
    fun showErrorToast(
        error: Error
    ) {
        notificationManager.show(error.toNotificationModel())
    }
}
