package skdev.omsrings.mobile.ui.components.notification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.ui.theme.CustomTheme
import skdev.omsrings.mobile.ui.theme.values.Dimens
import skdev.omsrings.mobile.ui.theme.values.Elevation
import skdev.omsrings.mobile.utils.notification.NotificationModel
import skdev.omsrings.mobile.utils.notification.ToastType
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationItem(
    notification: NotificationModel,
    onDismiss: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val swipeState = rememberSwipeToDismissBoxState(
        positionalThreshold = { it * .4f }
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    LaunchedEffect(notification) {
        coroutineScope.launch {
            delay(3.seconds)
            isVisible = false
            delay(300)
            onDismiss()
        }
    }

    LaunchedEffect(swipeState.currentValue) {
        when(swipeState.currentValue) {
            SwipeToDismissBoxValue.StartToEnd -> onDismiss()
            SwipeToDismissBoxValue.EndToStart -> onDismiss()
            SwipeToDismissBoxValue.Settled -> {}
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally (
            initialOffsetX = { it },
            animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
        ) + expandHorizontally (
            animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(durationMillis = 300)),
        exit = slideOutHorizontally (
            targetOffsetX = { -it },
            animationSpec = tween(durationMillis = 300, easing = FastOutLinearInEasing)
        ) + shrinkHorizontally(
            animationSpec = tween(durationMillis = 300, easing = FastOutLinearInEasing)
        ) + fadeOut(animationSpec = tween(durationMillis = 300))
    ) {
        SwipeToDismissBox(
            modifier = Modifier.fillMaxWidth(),
            state = swipeState,
            backgroundContent = {}
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.spaceSmall),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = Elevation.defaultElevation,
                ),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = when (notification) {
                        is NotificationModel.Error -> CustomTheme.colors.errorContainer
                        is NotificationModel.Toast -> when (notification.type) {
                            ToastType.Success -> CustomTheme.colors.successContainer
                            ToastType.Warning -> CustomTheme.colors.warningContainer
                            ToastType.Info -> MaterialTheme.colorScheme.surfaceContainer
                        }
                        else -> MaterialTheme.colorScheme.surfaceContainer
                    },
                    contentColor = when (notification) {
                        is NotificationModel.Error -> CustomTheme.colors.onErrorContainer
                        is NotificationModel.Toast -> when (notification.type) {
                            ToastType.Success -> CustomTheme.colors.onSuccessContainer
                            ToastType.Warning -> CustomTheme.colors.onWarningContainer
                            ToastType.Info -> MaterialTheme.colorScheme.onSurface
                        }
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                )
            ) {
                when (notification) {
                    is NotificationModel.Error -> NotificationContent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.spaceMedium),
                        title = stringResource(notification.titleRes),
                        message = stringResource(notification.messageRes),
                        icon = notification.icon
                    )

                    is NotificationModel.Toast -> NotificationContent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.spaceMedium),
                        title = stringResource(notification.titleRes),
                        message = stringResource(notification.messageRes),
                        icon = notification.icon,
                    )

                    is NotificationModel.Custom -> notification.content(
                        Modifier
                            .fillMaxWidth()
                            .padding(Dimens.spaceMedium),
                    )
                }
            }
        }
    }
}
