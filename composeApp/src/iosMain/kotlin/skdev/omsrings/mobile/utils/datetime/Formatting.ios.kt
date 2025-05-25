package skdev.omsrings.mobile.utils.datetime

import io.github.aakira.napier.Napier
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toNSDateComponents
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSCalendar
import platform.Foundation.NSDate
import platform.Foundation.NSTimeZone

actual fun LocalDateTime.format(pattern: String, default: String): String {
    return try {
        val dateFormatter = NSDateFormatter()
        dateFormatter.dateFormat = pattern
        val components = this.toNSDateComponents()
        val calendar = NSCalendar.currentCalendar
        val date = calendar.dateFromComponents(components)
        checkNotNull(date)
        dateFormatter.stringFromDate(date)
    } catch (e: Exception) {
        Napier.e(tag = "Formatting") { "can't format ${this}, cause error -> $e" }
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
        val components = this.toNSDateComponents()
        val calendar = NSCalendar.currentCalendar
        val date = calendar.dateFromComponents(components)
        checkNotNull(date)
        dateFormatter.stringFromDate(date)
    } catch (e: Exception) {
        Napier.e(tag = "Formatting") { "can't format ${this}, cause error -> $e" }
        default
    }
}
