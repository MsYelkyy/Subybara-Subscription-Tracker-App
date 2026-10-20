package com.subybara.app.core.model

private const val WeeksPerYear = 52.0
private const val MonthsPerYear = 12.0
private const val MonthsPerQuarter = 3.0

/**
 * Number of days ahead that renewal reminders cover.
 */
const val RenewalReminderDays = 7

/**
 * Monthly cost of this subscription, converted from its billing cycle.
 */
fun Subscription.monthlyCost(): Double = when (billingCycle) {
    "Weekly" -> amount * WeeksPerYear / MonthsPerYear
    "Quarterly" -> amount / MonthsPerQuarter
    "Yearly" -> amount / MonthsPerYear
    else -> amount
}

/**
 * Subscriptions with the Active status.
 *
 * Paused and canceled subscriptions are not billed, so the spending and renewal
 * calculations shared by Home, Insights, Renewals, and Notifications only count these.
 */
fun List<Subscription>.activeSubscriptions(): List<Subscription> =
    filter { subscription -> subscription.status == SubscriptionStatus.ACTIVE }

/**
 * Total monthly cost of the active subscriptions.
 */
fun List<Subscription>.totalMonthlySpend(): Double =
    activeSubscriptions().sumOf { subscription -> subscription.monthlyCost() }

/**
 * Total yearly cost of the active subscriptions.
 */
fun List<Subscription>.totalAnnualSpend(): Double = totalMonthlySpend() * MonthsPerYear

/**
 * Amount billed during the calendar month that contains [todayMillis].
 *
 * Unlike [totalMonthlySpend], a yearly subscription only counts in the month it
 * renews, and a weekly one counts once for every billing day in the month.
 */
fun List<Subscription>.spendDueThisMonth(todayMillis: Long = todayInMillis()): Double {
    val monthStart = startOfMonth(todayMillis)
    val nextMonthStart = addBillingCycles(monthStart, "Monthly", 1)

    return activeSubscriptions().sumOf { subscription ->
        subscription.amount * subscription.billingDaysBetween(monthStart, nextMonthStart)
    }
}

/**
 * One category's monthly spend and its share of the total monthly spend.
 */
data class CategorySpend(
    val category: SubscriptionCategory,
    val monthlyAmount: Double,
    val share: Double
)

/**
 * Monthly spend per category for the active subscriptions, highest first.
 */
fun List<Subscription>.spendByCategory(): List<CategorySpend> {
    val total = totalMonthlySpend()

    return activeSubscriptions()
        .groupBy { subscription -> subscription.category }
        .map { (category, items) ->
            val amount = items.sumOf { subscription -> subscription.monthlyCost() }
            CategorySpend(
                category = category,
                monthlyAmount = amount,
                share = if (total > 0) amount / total else 0.0
            )
        }
        .sortedByDescending { categorySpend -> categorySpend.monthlyAmount }
}

/**
 * A subscription's next billing date and the number of days until it.
 */
data class UpcomingRenewal(
    val subscription: Subscription,
    val dateMillis: Long,
    val daysLeft: Int
) {
    /**
     * Identifies this reminder. A new key is created when the billing date moves
     * to the next cycle, so the next reminder counts as new.
     */
    val reminderKey: String
        get() = "${subscription.id}-$dateMillis"
}

/**
 * Active subscriptions ordered by their next billing date.
 *
 * @param withinDays When set, only renewals in the next [withinDays] days are returned.
 */
fun List<Subscription>.upcomingRenewals(
    withinDays: Int? = null,
    todayMillis: Long = todayInMillis()
): List<UpcomingRenewal> = activeSubscriptions()
    .mapNotNull { subscription ->
        subscription.nextBillingDate(todayMillis)?.let { dateMillis ->
            UpcomingRenewal(
                subscription = subscription,
                dateMillis = dateMillis,
                daysLeft = daysUntil(dateMillis, todayMillis)
            )
        }
    }
    .filter { renewal -> withinDays == null || renewal.daysLeft <= withinDays }
    .sortedBy { renewal -> renewal.dateMillis }

/**
 * Short countdown text such as "Today", "in 1 day", or "in 12 days".
 */
fun renewsInLabel(daysLeft: Int): String = when (daysLeft) {
    0 -> "Today"
    1 -> "in 1 day"
    else -> "in $daysLeft days"
}

/**
 * Month heading for a renewal on [dateMillis]: "This Month", "Next Month",
 * or the month and year, such as "December 2026".
 */
fun renewalMonthLabel(
    dateMillis: Long,
    todayMillis: Long = todayInMillis()
): String {
    val thisMonth = startOfMonth(todayMillis)

    return when (startOfMonth(dateMillis)) {
        thisMonth -> "This Month"
        addBillingCycles(thisMonth, "Monthly", 1) -> "Next Month"
        else -> formatDate(dateMillis, "MMMM yyyy")
    }
}
