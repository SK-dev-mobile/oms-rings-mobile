package skdev.omsrings.mobile.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalCustomColors = staticCompositionLocalOf {
    CustomColors(
        success = Color.Unspecified,
        successContainer = Color.Unspecified,
        onSuccessContainer = Color.Unspecified,
        error = Color.Unspecified,
        errorContainer = Color.Unspecified,
        onErrorContainer = Color.Unspecified,
        warning = Color.Unspecified,
        warningContainer = Color.Unspecified,
        onWarningContainer = Color.Unspecified,
    )
}

@Immutable
data class CustomColors(
    val success: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val error: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
)

val lightCustomThemeColors = CustomColors(
    success = custom_theme_light_success,
    successContainer = custom_theme_light_successContainer,
    onSuccessContainer = custom_theme_light_onSuccessContainer,
    error = custom_theme_light_error,
    errorContainer = custom_theme_light_errorContainer,
    onErrorContainer = custom_theme_light_onErrorContainer,
    warning = custom_theme_light_warning,
    warningContainer = custom_theme_light_warningContainer,
    onWarningContainer = custom_theme_light_onWarningContainer,
)

val darkCustomThemeColors = CustomColors(
    success = custom_theme_dark_success,
    successContainer = custom_theme_dark_successContainer,
    onSuccessContainer = custom_theme_dark_onSuccessContainer,
    error = custom_theme_dark_error,
    errorContainer = custom_theme_dark_errorContainer,
    onErrorContainer = custom_theme_dark_onErrorContainer,
    warning = custom_theme_dark_warning,
    warningContainer = custom_theme_dark_warningContainer,
    onWarningContainer = custom_theme_dark_onWarningContainer,
)

object CustomTheme {
    val colors: CustomColors
        @Composable
        get() = LocalCustomColors.current
}
