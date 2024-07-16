package skdev.omsrings.mobile.utils.datetime

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toNSDateComponents
import platform.Foundation.NSDateFormatter

actual fun LocalDateTime.format(pattern: String, default: String): String {
    return try {
        val dateFormatter = NSDateFormatter()
        dateFormatter.dateFormat = pattern
        val nsDate = this.toNSDateComponents().date
        checkNotNull(nsDate)
        dateFormatter.stringFromDate(nsDate)
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
actual fun LocalDate.format(
    pattern: String,
    default: String
): String {
    return try {
        val dateFormatter = NSDateFormatter()
        dateFormatter.dateFormat = pattern
        val nsDate = this.toNSDateComponents().date
        checkNotNull(nsDate)
        dateFormatter.stringFromDate(nsDate)
    } catch (e: Exception) {
        default
    }
}
