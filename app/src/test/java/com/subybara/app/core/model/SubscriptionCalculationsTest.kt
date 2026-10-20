package com.subybara.app.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SubscriptionCalculationsTest {

    private val today = date("October 20, 2026")

    private fun date(text: String): Long = requireNotNull(parseDate(text))

    private fun subscription(
        name: String = "Netflix",
        amount: Double = 549.0,
        billingCycle: String = "Monthly",
        renewalDate: String = "November 1, 2026",
        category: SubscriptionCategory = SubscriptionCategory.ENTERTAINMENT,
        status: SubscriptionStatus = SubscriptionStatus.ACTIVE,
        startDate: String = "January 1, 2025"
    ) = Subscription(
        name = name,
        amount = amount,
        billingCycle = billingCycle,
        renewalDate = renewalDate,
        category = category,
        status = status,
        startDate = startDate
    )

    @Test
    fun monthlyCostConvertsEachBillingCycle() {
        assertEquals(520.0, subscription(amount = 120.0, billingCycle = "Weekly").monthlyCost(), 0.001)
        assertEquals(149.0, subscription(amount = 149.0, billingCycle = "Monthly").monthlyCost(), 0.001)
        assertEquals(100.0, subscription(amount = 300.0, billingCycle = "Quarterly").monthlyCost(), 0.001)
        assertEquals(100.0, subscription(amount = 1200.0, billingCycle = "Yearly").monthlyCost(), 0.001)
    }

    @Test
    fun totalsCountOnlyActiveSubscriptions() {
        val subscriptions = listOf(
            subscription(amount = 549.0),
            subscription(name = "Spotify", amount = 149.0, status = SubscriptionStatus.PAUSED),
            subscription(name = "YouTube", amount = 159.0, status = SubscriptionStatus.CANCELED)
        )

        assertEquals(1, subscriptions.activeSubscriptions().size)
        assertEquals(549.0, subscriptions.totalMonthlySpend(), 0.001)
        assertEquals(6588.0, subscriptions.totalAnnualSpend(), 0.001)
    }

    @Test
    fun totalsConvertMixedBillingCycles() {
        val subscriptions = listOf(
            subscription(amount = 549.0, billingCycle = "Monthly"),
            subscription(name = "Adobe", amount = 1200.0, billingCycle = "Yearly"),
            subscription(name = "Gym", amount = 30.0, billingCycle = "Weekly")
        )

        assertEquals(779.0, subscriptions.totalMonthlySpend(), 0.001)
        assertEquals(9348.0, subscriptions.totalAnnualSpend(), 0.001)
    }

    @Test
    fun spendByCategoryGroupsSortsAndSharesTheTotal() {
        val subscriptions = listOf(
            subscription(name = "Spotify", amount = 149.0, category = SubscriptionCategory.MUSIC),
            subscription(name = "Netflix", amount = 549.0),
            subscription(name = "YouTube", amount = 159.0, category = SubscriptionCategory.MUSIC),
            subscription(
                name = "Paused Music",
                amount = 500.0,
                category = SubscriptionCategory.MUSIC,
                status = SubscriptionStatus.PAUSED
            )
        )

        val categories = subscriptions.spendByCategory()

        assertEquals(
            listOf(SubscriptionCategory.ENTERTAINMENT, SubscriptionCategory.MUSIC),
            categories.map { it.category }
        )
        assertEquals(549.0, categories[0].monthlyAmount, 0.001)
        assertEquals(308.0, categories[1].monthlyAmount, 0.001)
        assertEquals(549.0 / 857.0, categories[0].share, 0.0001)
        assertEquals(1.0, categories.sumOf { it.share }, 0.0001)
    }

    @Test
    fun emptyOrInactiveListsProduceZeroWithoutErrors() {
        val inactive = listOf(subscription(status = SubscriptionStatus.CANCELED))

        assertEquals(0.0, emptyList<Subscription>().totalMonthlySpend(), 0.0)
        assertEquals(0.0, inactive.totalAnnualSpend(), 0.0)
        assertTrue(inactive.spendByCategory().isEmpty())
        assertEquals(0.0, inactive.spendDueThisMonth(today), 0.0)
    }

    @Test
    fun spendDueThisMonthCountsOnlyBillingDaysInsideTheMonth() {
        val subscriptions = listOf(
            subscription(amount = 549.0, renewalDate = "November 1, 2026"),
            subscription(name = "Adobe", amount = 2600.0, billingCycle = "Yearly", renewalDate = "December 15, 2026"),
            subscription(name = "Domain", amount = 900.0, billingCycle = "Yearly", renewalDate = "October 25, 2026"),
            subscription(name = "Gym", amount = 100.0, billingCycle = "Weekly", renewalDate = "October 21, 2026")
        )

        assertEquals(549.0 + 900.0 + 400.0, subscriptions.spendDueThisMonth(today), 0.001)
    }

    @Test
    fun spendDueThisMonthSkipsBillingDaysBeforeTheStartDate() {
        val startedToday = subscription(renewalDate = "November 5, 2026", startDate = "October 20, 2026")

        assertEquals(0.0, listOf(startedToday).spendDueThisMonth(today), 0.0)
    }
}
