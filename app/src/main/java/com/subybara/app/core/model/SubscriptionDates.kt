package com.subybara.app.core.model

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Dates use midnight UTC to match the Material date picker.

private const val StoredDatePattern = "MMMM d, yyyy"
private const val MillisPerDay = 24L * 60L * 60L * 1000L
private const val DaysPerWeek = 7
private const val MonthsPerQuarter = 3
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
fun parseDate(text: String): Long? {
    val position = ParsePosition(0)
    val date = dateFormat(StoredDatePattern).parse(text, position)
    return date?.takeIf { position.index == text.length }?.time
}

/**
 * Returns the number of days until [dateMillis], or a negative value for past dates.
 */
fun daysUntil(dateMillis: Long, todayMillis: Long = todayInMillis()): Int =
    ((dateMillis - todayMillis) / MillisPerDay).toInt()

/**
 * Returns the first day of the month that contains [dateMillis].
 */
fun startOfMonth(dateMillis: Long): Long = Calendar.getInstance(Utc).apply {
    timeInMillis = dateMillis
    set(Calendar.DAY_OF_MONTH, 1)
}.timeInMillis

/**
 * Moves [dateMillis] forward (or back, for a negative [count]) by whole billing cycles.
 *
 * Every step is counted from [dateMillis] itself, so a date on the 31st lands on
 * the last day of shorter months without drifting earlier in later months.
 */
fun addBillingCycles(
    dateMillis: Long,
    billingCycle: String,
    count: Int
): Long = Calendar.getInstance(Utc).apply {
    timeInMillis = dateMillis
    when (billingCycle) {
        "Weekly" -> add(Calendar.DAY_OF_MONTH, DaysPerWeek * count)
        "Quarterly" -> add(Calendar.MONTH, MonthsPerQuarter * count)
        "Yearly" -> add(Calendar.YEAR, count)
        else -> add(Calendar.MONTH, count)
    }
}.timeInMillis

/**
 * Returns the billing date to show for this subscription.
 *
 * For an active subscription, a recorded date that has already passed is moved
 * forward by its billing cycle until it is today or later. Paused and canceled
 * subscriptions keep their recorded date. Returns null if the date cannot be read.
 */
fun Subscription.nextBillingDate(todayMillis: Long = todayInMillis()): Long? {
    val recordedDate = parseDate(renewalDate) ?: return null
    if (status != SubscriptionStatus.ACTIVE) return recordedDate

    var cycles = 0
    var billingDate = recordedDate
    while (billingDate < todayMillis) {
        cycles++
        billingDate = addBillingCycles(recordedDate, billingCycle, cycles)
    }
    return billingDate
}

/**
 * Counts the billing days from [fromMillis] (inclusive) to [untilMillis] (exclusive).
 *
 * Billing days before the subscription's start date are skipped.
 */
fun Subscription.billingDaysBetween(fromMillis: Long, untilMillis: Long): Int {
    val recordedDate = parseDate(renewalDate) ?: return 0
    val startedOn = parseDate(startDate) ?: Long.MIN_VALUE

    var cycles = 0
    while (addBillingCycles(recordedDate, billingCycle, cycles) >= fromMillis) {
        cycles--
    }
    while (addBillingCycles(recordedDate, billingCycle, cycles) < fromMillis) {
        cycles++
    }

    var billingDays = 0
    var billingDate = addBillingCycles(recordedDate, billingCycle, cycles)
    while (billingDate < untilMillis) {
        if (billingDate >= startedOn) {
            billingDays++
        }
        cycles++
        billingDate = addBillingCycles(recordedDate, billingCycle, cycles)
    }
    return billingDays
}

/**
 * Reformats a stored date. Returns [text] unchanged if parsing fails.
 */
fun reformatDate(
    text: String,
    pattern: String
): String = parseDate(text)?.let { dateMillis ->
    formatDate(dateMillis, pattern)
} ?: text
