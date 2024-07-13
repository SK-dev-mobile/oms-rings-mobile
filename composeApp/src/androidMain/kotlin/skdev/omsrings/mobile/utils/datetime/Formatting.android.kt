package skdev.omsrings.mobile.utils.datetime

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.text.capitalize
import io.github.aakira.napier.Napier
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale


@SuppressLint("NewApi")
actual fun LocalDateTime.format(pattern: String, default: String): String {
    return try {
        val dtf = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
        this.toJavaLocalDateTime().format(dtf).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    } catch (e: Exception) {
        default
    }
}

/**
 * Приведение местной даты в нужный формат
 *
 * @param pattern шаблон формата даты, смотри [DateTimePattern]
 * @param default значение по умолчанию
 */
@SuppressLint("NewApi")
actual fun LocalDate.format(
    pattern: String,
    default: String
): String {
    return try {
        val dtf = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
        this.toJavaLocalDate().format(dtf).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    } catch (e: Exception) {
        Napier.e(tag = "LocalDateFormat", throwable = e) { e.message.toString() }
        default
    }
}
