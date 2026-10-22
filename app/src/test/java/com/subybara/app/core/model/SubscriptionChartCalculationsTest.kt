package com.subybara.app.core.model

import androidx.compose.runtime.snapshots.Snapshot
import com.subybara.app.core.data.SubscriptionStore
import org.junit.Assert.*
import org.junit.Test

class SubscriptionChartCalculationsTest {
    private fun date(value: String) = requireNotNull(parseDate(value))
    private val today = date("December 1, 2026")
    private fun subscription(
        amount: Double = 100.0,
        cycle: String = "Monthly",
        renewal: String = "December 15, 2026",
        category: SubscriptionCategory = SubscriptionCategory.MUSIC,
        status: SubscriptionStatus = SubscriptionStatus.ACTIVE
    ) = Subscription(name = "Test", amount = amount, billingCycle = cycle,
        renewalDate = renewal, category = category, status = status)

    @Test fun sixMonthsCrossTheYearBoundaryWithDynamicLabels() {
        val months = emptyList<Subscription>().projectedMonthlyBills(today)
        assertEquals(listOf("Dec", "Jan", "Feb", "Mar", "Apr", "May"), months.map { it.label })
        assertEquals("January 2027", months[1].fullLabel)
        assertEquals(6, months.size)
        assertTrue(months.all { it.amount == 0.0 })
    }

    @Test fun monthlyBillsStartAtNextRenewalWithoutBackfilling() {
        val months = listOf(subscription(renewal = "January 15, 2027")).projectedMonthlyBills(today)
        assertEquals(listOf(0.0, 100.0, 100.0, 100.0, 100.0, 100.0), months.map { it.amount })
    }

    @Test fun weeklyCountsEveryBillingDayIncludingFiveWeekMonths() {
        val months = listOf(subscription(cycle = "Weekly", renewal = "December 1, 2026"))
            .projectedMonthlyBills(today)
        assertEquals(listOf(500.0, 400.0, 400.0, 500.0, 400.0, 400.0), months.map { it.amount })
    }

    @Test fun quarterlyAndYearlyAreBilledInRenewalMonthsOnly() {
        val months = listOf(subscription(300.0, "Quarterly"),
            subscription(1200.0, "Yearly", "January 1, 2027")).projectedMonthlyBills(today)
        assertEquals(listOf(300.0, 1200.0, 0.0, 300.0, 0.0, 0.0), months.map { it.amount })
    }

    @Test fun currentMonthIncludesTodayButSkipsEarlierBills() {
        val months = listOf(subscription(cycle = "Weekly", renewal = "October 1, 2026"))
            .projectedMonthlyBills(date("October 22, 2026"))
        assertEquals(200.0, months.first().amount, 0.0) // 22nd and 29th
    }

    @Test fun oldMonthEndAnchorsAndLeapYearsDoNotDrift() {
        val months = listOf(subscription(renewal = "January 31, 2024"))
            .projectedMonthlyBills(date("March 30, 2024"))
        assertEquals(100.0, months.first().amount, 0.0) // March 31, not March 29
        val leapDay = listOf(subscription(cycle = "Yearly", renewal = "February 29, 2024"))
            .projectedMonthlyBills(date("February 28, 2025"))
        assertEquals(100.0, leapDay.first().amount, 0.0)
    }

    @Test fun respectsStartDateAndExclusiveEndOfWindow() {
        val items = listOf(subscription(renewal = "December 1, 2026").copy(startDate = "February 1, 2027"),
            subscription(cycle = "Yearly", renewal = "June 1, 2027"))
        assertEquals(listOf(0.0, 0.0, 100.0, 100.0, 100.0, 100.0),
            items.projectedMonthlyBills(today).map { it.amount })
    }

    @Test fun pausedCanceledAndInvalidDatesDoNotAddBills() {
        val data = listOf(subscription(status = SubscriptionStatus.PAUSED),
            subscription(status = SubscriptionStatus.CANCELED), subscription(renewal = "Invalid"))
            .subscriptionChartData(today)
        assertTrue(data.months.all { it.amount == 0.0 })
        assertEquals(1, data.invalidRenewalCount)
        assertEquals(1, data.activeCount)
    }

    @Test fun categoryTotalsUseSharedMonthlyConventionsAndExcludeInactive() {
        val data = listOf(subscription(12.0, "Weekly"), subscription(300.0, "Quarterly"),
            subscription(1200.0, "Yearly", category = SubscriptionCategory.ENTERTAINMENT),
            subscription(50.0, category = SubscriptionCategory.ENTERTAINMENT),
            subscription(999.0, status = SubscriptionStatus.PAUSED),
            subscription(999.0, status = SubscriptionStatus.CANCELED)).subscriptionChartData(today)
        assertEquals(302.0, data.estimatedMonthlyCost, 0.0001)
        assertEquals(listOf(152.0, 150.0), data.categories.map { it.monthlyAmount })
        assertEquals(1.0, data.categories.sumOf { it.share }, 0.0001)
        assertEquals(100, data.categories.sumOf { it.percentage })
    }

    @Test fun roundedCategoryAmountsAndPercentagesStayConsistent() {
        val data = listOf(SubscriptionCategory.MUSIC, SubscriptionCategory.GAMING, SubscriptionCategory.OTHERS)
            .map { subscription(1.0, "Quarterly", category = it) }.subscriptionChartData(today)
        assertEquals(0.99, data.estimatedMonthlyCost, 0.0001)
        assertEquals(listOf(34, 33, 33), data.categories.map { it.percentage })
        assertEquals(data.estimatedMonthlyCost, data.categories.sumOf { it.monthlyAmount }, 0.0)
    }

    @Test fun emptyAndZeroCategoriesAreSafe() {
        val empty = emptyList<Subscription>().subscriptionChartData(today)
        assertTrue(empty.categories.isEmpty())
        assertEquals(0, empty.activeCount)
        val zero = listOf(subscription(0.0)).subscriptionChartData(today)
        assertEquals(0.0, zero.estimatedMonthlyCost, 0.0)
        assertEquals(0.0, zero.categories.single().share, 0.0)
        assertEquals(0, zero.categories.single().percentage)
    }

    @Test fun storeEditsStatusChangesDeletionAndUndoRecalculateCharts() {
        val snapshot = Snapshot.takeMutableSnapshot()
        try {
            snapshot.enter {
                SubscriptionStore.subscriptions.toList().forEach { SubscriptionStore.remove(it.id) }
                fun chart() = SubscriptionStore.subscriptions.subscriptionChartData(today)
                SubscriptionStore.add(subscription())
                val saved = SubscriptionStore.subscriptions.single()
                assertEquals(100.0, chart().months.first().amount, 0.0)
                SubscriptionStore.update(saved.copy(amount = 200.0, category = SubscriptionCategory.GAMING,
                    renewalDate = "January 1, 2027"))
                assertEquals(0.0, chart().months.first().amount, 0.0)
                assertEquals(200.0, chart().months[1].amount, 0.0)
                assertEquals(SubscriptionCategory.GAMING, chart().categories.single().category)
                listOf(SubscriptionStatus.PAUSED, SubscriptionStatus.CANCELED).forEach {
                    SubscriptionStore.changeStatus(saved.id, it)
                    assertTrue(chart().categories.isEmpty())
                    assertTrue(chart().months.all { month -> month.amount == 0.0 })
                }
                SubscriptionStore.changeStatus(saved.id, SubscriptionStatus.ACTIVE)
                assertEquals(200.0, chart().estimatedMonthlyCost, 0.0)
                SubscriptionStore.remove(saved.id)
                assertEquals(0, chart().activeCount)
                SubscriptionStore.restore(saved, 0)
                assertEquals(100.0, chart().months.first().amount, 0.0)
            }
        } finally { snapshot.dispose() }
    }
}
