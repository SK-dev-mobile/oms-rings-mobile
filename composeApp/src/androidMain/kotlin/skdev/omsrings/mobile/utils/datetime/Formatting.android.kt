package skdev.omsrings.mobile.utils.datetime

import android.annotation.SuppressLint
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDate
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

/**
 * Приведение местной даты в нужный формат
 *
 * @param pattern шаблон формата даты, смотри [DateTimePattern]
 * @param default значение по умолчанию
 */
@SuppressLint("SimpleDateFormat")
actual fun LocalDate.format(
    pattern: String,
    default: String
): String {
    return try {
        SimpleDateFormat(pattern).format(this.toJavaLocalDate())
    } catch (e: Exception) {
        default
    }
}
