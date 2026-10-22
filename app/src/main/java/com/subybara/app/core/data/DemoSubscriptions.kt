package com.subybara.app.core.data

import com.subybara.app.core.model.*

/** Evaluation fixtures, created only on the first successful demo login. */
internal fun demoSubscriptions(todayMillis: Long = todayInMillis()): List<Subscription> {
    fun record(id: Int, name: String, amount: Double, cycle: String,
        daysAhead: Int, category: SubscriptionCategory) = Subscription(
        id = id, name = name, amount = amount, billingCycle = cycle,
        renewalDate = formatDate(todayMillis + daysAhead * 86_400_000L),
        category = category, status = SubscriptionStatus.ACTIVE,
        paymentMethod = "GCash", startDate = formatDate(todayMillis)
    )
    return listOf(
        record(1, "Netflix", 549.0, "Monthly", 0, SubscriptionCategory.ENTERTAINMENT),
        record(2, "Spotify Premium", 149.0, "Monthly", 2, SubscriptionCategory.MUSIC),
        record(3, "YouTube Premium", 159.0, "Monthly", 5, SubscriptionCategory.ENTERTAINMENT),
        record(4, "Adobe Creative Cloud", 2600.0, "Monthly", 12, SubscriptionCategory.PRODUCTIVITY),
        record(5, "Google One 200GB", 390.0, "Quarterly", 18, SubscriptionCategory.CLOUD_STORAGE),
        record(6, "Strava", 99.0, "Weekly", 3, SubscriptionCategory.HEALTH_FITNESS),
        record(7, "Canva Pro", 3490.0, "Yearly", 45, SubscriptionCategory.PRODUCTIVITY)
    )
}
