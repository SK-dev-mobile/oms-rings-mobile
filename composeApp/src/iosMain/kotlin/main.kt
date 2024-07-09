import androidx.compose.ui.window.ComposeUIViewController
import skdev.omsrings.mobile.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
