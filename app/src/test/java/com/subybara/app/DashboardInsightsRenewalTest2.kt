package com.subybara.app

import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.SubscriptionCategory
import com.subybara.app.core.model.SubscriptionStatus
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardInsightsRenewalTest2 {

    private lateinit var mockSubscriptions: List<Subscription>

    @Before
    fun setUp() {
        mockSubscriptions = listOf(
            Subscription(
                id = 1,
                name = "Netflix",
                amount = 549.00,
                billingCycle = "Monthly",
                renewalDate = "2026-11-01",
                category = SubscriptionCategory.ENTERTAINMENT,
                status = SubscriptionStatus.ACTIVE
            ),
            Subscription(
                id = 2,
                name = "Spotify",
                amount = 149.00,
                billingCycle = "Monthly",
                renewalDate = "2026-10-25",
                category = SubscriptionCategory.MUSIC,
                status = SubscriptionStatus.ACTIVE
            ),
            Subscription(
                id = 3,
                name = "Adobe Creative Cloud",
                amount = 2600.00,
                billingCycle = "Monthly",
                renewalDate = "2026-10-22",
                category = SubscriptionCategory.PRODUCTIVITY,
                status = SubscriptionStatus.ACTIVE
            )
        )
    }

    // HomeScreen.kt (Dashboard) Tests

    @Test
    fun homeScreen_monthlyAndAnnualSpend_calculatedCorrectly() {
        val totalMonthlySpend = mockSubscriptions.sumOf { it.amount }
        val totalAnnualSpend = totalMonthlySpend * 12

        assertEquals(3298.00, totalMonthlySpend, 0.01)
        assertEquals(39576.00, totalAnnualSpend, 0.01)
    }

    @Test
    fun homeScreen_activeSubscriptionCount_matchesListSize() {
        val activeCount = mockSubscriptions.filter { it.status == SubscriptionStatus.ACTIVE }.size
        assertEquals(3, activeCount)
    }

    // InsightsScreen.kt Tests

    @Test
    fun insightsScreen_groupByCategory_sumsAmountsCorrectly() {
        val categorySummaries = mockSubscriptions
            .groupBy { it.category.label }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        assertEquals(549.00, categorySummaries[SubscriptionCategory.ENTERTAINMENT.label]!!, 0.01)
        assertEquals(149.00, categorySummaries[SubscriptionCategory.MUSIC.label]!!, 0.01)
        assertEquals(2600.00, categorySummaries[SubscriptionCategory.PRODUCTIVITY.label]!!, 0.01)
    }

    @Test
    fun insightsScreen_topCategoryPercentage_computedAccurately() {
        val totalSpend = mockSubscriptions.sumOf { it.amount }
        val topCategorySpend = mockSubscriptions
            .groupBy { it.category.label }
            .maxByOrNull { it.value.sumOf { item -> item.amount } }
            ?.value?.sumOf { it.amount } ?: 0.0

        val topCategoryPercentage = ((topCategorySpend / totalSpend) * 100).toInt()

        // Adobe (2600.00) / Total (3298.00) * 100 = ~78%
        assertEquals(78, topCategoryPercentage)
    }

    // UpcomingRenewalsScreen.kt Tests

    @Test
    fun upcomingRenewals_sortingByRenewalDate_ordersChronologically() {
        val sorted = mockSubscriptions.sortedBy { it.renewalDate }

        assertEquals("Adobe Creative Cloud", sorted[0].name) // 2026-10-22
        assertEquals("Spotify", sorted[1].name)              // 2026-10-25
        assertEquals("Netflix", sorted[2].name)              // 2026-11-01
    }

    @Test
    fun upcomingRenewals_dateParsingHelper_formatsDayAndMonthBadges() {
        val rawDate = "2026-10-22"
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = sdf.parse(rawDate) ?: Date()

        val dayFormatted = SimpleDateFormat("d", Locale.US).format(date)
        val monthFormatted = SimpleDateFormat("MMM", Locale.US).format(date).uppercase()

        assertEquals("22", dayFormatted)
        assertEquals("OCT", monthFormatted)
    }
}