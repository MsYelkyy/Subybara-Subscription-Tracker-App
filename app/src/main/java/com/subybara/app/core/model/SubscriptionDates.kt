package com.subybara.app.core.model

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Dates use midnight UTC to match the Material date picker.

private const val StoredDatePattern = "MMMM d, yyyy"
private const val MillisPerDay = 24L * 60L * 60L * 1000L
private val Utc: TimeZone = TimeZone.getTimeZone("UTC")

private fun dateFormat(pattern: String): SimpleDateFormat =
    SimpleDateFormat(pattern, Locale.ENGLISH).apply {
        timeZone = Utc
        isLenient = false
    }

/**
 * Returns today in the device time zone as midnight UTC.
 */
fun todayInMillis(): Long {
    val local = Calendar.getInstance()

    return Calendar.getInstance(Utc).apply {
        clear()
        set(
            local.get(Calendar.YEAR),
            local.get(Calendar.MONTH),
            local.get(Calendar.DAY_OF_MONTH)
        )
    }.timeInMillis
}

/**
 * Formats [dateMillis] using [pattern], or the stored date format by default.
 */
fun formatDate(
    dateMillis: Long,
    pattern: String = StoredDatePattern
): String = dateFormat(pattern).format(Date(dateMillis))

/**
 * Parses a stored date. Returns null if parsing fails.
 */
fun parseDate(text: String): Long? =
    try {
        dateFormat(StoredDatePattern).parse(text)?.time
    } catch (_: ParseException) {
        null
    }

/**
 * Returns the number of days until [dateMillis], or a negative value for past dates.
 */
fun daysUntil(dateMillis: Long): Int =
    ((dateMillis - todayInMillis()) / MillisPerDay).toInt()

/**
 * Reformats a stored date. Returns [text] unchanged if parsing fails.
 */
fun reformatDate(
    text: String,
    pattern: String
): String = parseDate(text)?.let { dateMillis ->
    formatDate(dateMillis, pattern)
} ?: text
