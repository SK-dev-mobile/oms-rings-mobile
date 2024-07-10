package skdev.omsrings.mobile.utils.datetime

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