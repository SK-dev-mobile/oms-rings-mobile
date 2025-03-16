package skdev.omsrings.mobile

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.permissionUtil
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinContext
import skdev.omsrings.mobile.app.App
import skdev.omsrings.mobile.di.initKoin

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
        Firebase.initialize(this@AndroidApp)
        Napier.base(DebugAntilog())
        NotifierManager.initialize(
            configuration = NotificationPlatformConfiguration.Android(
                notificationIconResId = com.google.android.gms.base.R.drawable.common_google_signin_btn_icon_light,
            )
        )
    }
}

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissionUtil by permissionUtil()
        permissionUtil.askNotificationPermission()

        enableEdgeToEdge()
        setContent {
            KoinContext {
                App()
            }
        }
    }
}
