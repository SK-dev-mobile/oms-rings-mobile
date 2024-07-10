package skdev.omsrings.mobile.utils.datetime

import android.annotation.SuppressLint
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
actual fun LocalDateTime.format(pattern: String, default: String): String {
    return try {
        SimpleDateFormat(pattern).format(this.toJavaLocalDateTime())
    } catch (e: Exception) {
        default
    }
}
