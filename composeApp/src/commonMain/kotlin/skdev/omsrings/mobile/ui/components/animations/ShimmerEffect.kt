package skdev.omsrings.mobile.ui.components.animations

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

fun Modifier.shimmerEffect(enabled: Boolean = true, shape: Shape = RectangleShape): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "shimer_transition")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1500)
        ),
        label = "shimmer_modifier"
    )
    then(
        if (enabled) {
            background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        Color(
                            MaterialTheme.colorScheme.onBackground.red,
                            MaterialTheme.colorScheme.onBackground.green,
                            MaterialTheme.colorScheme.onBackground.blue,
                            0.2f
                        ),
                        MaterialTheme.colorScheme.background
                    ),
                    start = Offset(startOffsetX, 0f),
                    end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
                ),
                shape = shape
            ).onGloballyPositioned {
                size = it.size
            }
        } else {
            this
        }
    )
}


@Composable
fun ShimmerPlaceHolder(modifier: Modifier = Modifier, shape: Shape = MaterialTheme.shapes.medium) {
    Box(
        modifier = modifier
            .clip(shape)
            .shimmerEffect()
    )
}
