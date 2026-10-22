package com.subybara.app.core.model

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.floor

data class ProjectedMonth(val startMillis: Long, val amount: Double) {
    val label: String get() = formatDate(startMillis, "MMM")
    val fullLabel: String get() = formatDate(startMillis, "MMMM yyyy")
}

data class CategoryEstimate(
    val category: SubscriptionCategory,
    val monthlyAmount: Double,
    val share: Double,
    val percentage: Int
)

data class SubscriptionChartData(
    val months: List<ProjectedMonth>,
    val categories: List<CategoryEstimate>,
    val activeCount: Int,
    val invalidRenewalCount: Int
) {
    val estimatedMonthlyCost: Double get() = categories.sumOf { it.monthlyAmount }
    val estimatedAnnualCost: Double get() = estimatedMonthlyCost * 12
}

/**
 * Six calendar months starting this month. Only bills on/after today and the
 * recorded next renewal are projected; no earlier bills are invented. An old
 * renewal rolls forward from its original anchor, preserving month ends/leap days.
 * The first month is the remainder of this month, not a payment history.
 */
fun List<Subscription>.projectedMonthlyBills(
    todayMillis: Long = todayInMillis()
): List<ProjectedMonth> {
    val firstMonth = startOfMonth(todayMillis)
    val boundaries = (0..6).map { addBillingCycles(firstMonth, "Monthly", it) }
    val amounts = MutableList(6) { BigDecimal.ZERO }
    activeSubscriptions().forEach { subscription ->
        val anchor = parseDate(subscription.renewalDate) ?: return@forEach
        val earliest = maxOf(todayMillis, parseDate(subscription.startDate) ?: todayMillis)
        var cycle = 0
        var date = anchor
        while (date < earliest) {
            date = addBillingCycles(anchor, subscription.billingCycle, ++cycle)
        }
        while (date < boundaries.last()) {
            val month = (0..5).first { date < boundaries[it + 1] }
            amounts[month] = amounts[month].add(BigDecimal.valueOf(subscription.amount))
            date = addBillingCycles(anchor, subscription.billingCycle, ++cycle)
        }
    }
    return (0..5).map { ProjectedMonth(boundaries[it], amounts[it].setScale(2, RoundingMode.HALF_UP).toDouble()) }
}

/** Uses the existing monthly normalization; rounds category amounts to display cents.
 * Whole percentages use largest remainders so the displayed shares sum to 100%.
 */
fun List<Subscription>.estimatedCategories(): List<CategoryEstimate> {
    val groups = spendByCategory().map { group ->
        group.category to BigDecimal.valueOf(group.monthlyAmount)
            .setScale(2, RoundingMode.HALF_UP).toDouble()
    }
    val total = groups.sumOf { it.second }
    val shares = groups.map { if (total > 0) it.second / total else 0.0 }
    val percentages = shares.map { floor(it * 100).toInt() }.toMutableList()
    if (total > 0) {
        shares.indices.sortedByDescending { shares[it] * 100 - percentages[it] }
            .take(100 - percentages.sum()).forEach { percentages[it]++ }
    }
    return groups.mapIndexed { index, (category, amount) ->
        CategoryEstimate(category, amount, shares[index], percentages[index])
    }
}

fun List<Subscription>.subscriptionChartData(todayMillis: Long = todayInMillis()) = SubscriptionChartData(
    months = projectedMonthlyBills(todayMillis),
    categories = estimatedCategories(),
    activeCount = activeSubscriptions().size,
    invalidRenewalCount = activeSubscriptions().count { parseDate(it.renewalDate) == null }
)
