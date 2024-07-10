package skdev.omsrings.mobile.ui.theme.values

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable


@Immutable
object AnimationSpec {
    val tweenFast: TweenSpec<Float> = tween(250)
    val tweeSlow: TweenSpec<Float> = tween(550)
    val springHigh = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    val springSlow = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessHigh
    )
}
