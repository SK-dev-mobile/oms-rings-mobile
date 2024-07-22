/*
 * Copyright (c) 2024. Artem Sukhanov (Stakancheck)
 * All rights reserved.
 *
 * This code is the property of Artem Sukhanov and is a commercial development.
 *
 * For inquiries, please contact:
 * Corporate Email: support@sk-dev.site
 * Personal Email: stakancheck@gmail.com
 */

package skdev.omsrings.mobile.utils.datetime

import io.github.aakira.napier.Napier
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime


/**
 * Приведение местного времени в нужный формат
 *
 * @param pattern шаблон формата даты, смотри [DateTimePattern]
 * @param default значение по умолчанию
 */
expect fun LocalDateTime.format(pattern: String, default: String = ""): String

fun LocalDateTime.format(pattern: DateTimePattern, default: String = ""): String =
    format(pattern.patternValue, default)

/**
 * Приведение местной даты в нужный формат
 *
 * @param pattern шаблон формата даты, смотри [DateTimePattern]
 * @param default значение по умолчанию
 */
expect fun LocalDate.format(pattern: String, default: String = ""): String

fun LocalDate.format(pattern: DateTimePattern, default: String = ""): String {
    val r = format(pattern.patternValue, default)
    return r
}

/**
 * Шаблоны для приведения дат в строку
 *
 * CALENDAR_MONTH_YEAR – месяц и год, пример: "Март 2024"
 * DAY_MONTH_YEAR – день, месяц и год, пример: "12 Март 2024"
 * SIMPLE_DATE – день, месяц и год, пример: "12.03.2024"
 * FULL_DATE_TIME – день, месяц, год, часы, минуты и секунды, пример: "12.03.2024 12:30:45"
 */
enum class DateTimePattern(val patternValue: String) {
    CALENDAR_MONTH_YEAR("LLLL, yyyy"),
    DAY_MONTH_YEAR("dd MMMM yyyy"),
    SIMPLE_DATE("dd.MM.yyyy"),
    FULL_DATE_TIME("dd.MM.yyyy HH:mm:ss"),
    FULL_DATE_TIME_WITHOUT_SECONDS("dd.MM.yyyy HH:mm"),
    SIMPLE_TIME("HH:mm")
}
