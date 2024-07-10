import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import skdev.omsrings.mobile.app.App

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
