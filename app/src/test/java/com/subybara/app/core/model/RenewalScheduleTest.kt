package com.subybara.app.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RenewalScheduleTest {

    private val today = date("October 20, 2026")

    private fun date(text: String): Long = requireNotNull(parseDate(text))

    private fun subscription(
        name: String = "Netflix",
        billingCycle: String = "Monthly",
        renewalDate: String = "November 1, 2026",
        status: SubscriptionStatus = SubscriptionStatus.ACTIVE,
        id: Int = 1
    ) = Subscription(
        id = id,
        name = name,
        amount = 549.0,
        billingCycle = billingCycle,
        renewalDate = renewalDate,
        category = SubscriptionCategory.ENTERTAINMENT,
        status = status
    )

    @Test
    fun futureBillingDateIsKept() {
        assertEquals(date("November 1, 2026"), subscription().nextBillingDate(today))
    }

    @Test
    fun passedMonthlyDateMovesToTheNextCycleAfterToday() {
        val subscription = subscription(renewalDate = "September 15, 2026")

        assertEquals(date("November 15, 2026"), subscription.nextBillingDate(today))
    }

    @Test
    fun passedWeeklyQuarterlyAndYearlyDatesMoveByTheirCycle() {
        val weekly = subscription(billingCycle = "Weekly", renewalDate = "October 1, 2026")
        val quarterly = subscription(billingCycle = "Quarterly", renewalDate = "May 10, 2026")
        val yearly = subscription(billingCycle = "Yearly", renewalDate = "March 3, 2025")

        assertEquals(date("October 22, 2026"), weekly.nextBillingDate(today))
        assertEquals(date("November 10, 2026"), quarterly.nextBillingDate(today))
        assertEquals(date("March 3, 2027"), yearly.nextBillingDate(today))
    }

    @Test
    fun monthEndDatesDoNotDriftEarlier() {
        val januaryEnd = date("January 31, 2026")

        assertEquals(date("February 28, 2026"), addBillingCycles(januaryEnd, "Monthly", 1))
        assertEquals(date("March 31, 2026"), addBillingCycles(januaryEnd, "Monthly", 2))
        assertEquals(
            date("March 31, 2026"),
            subscription(renewalDate = "January 31, 2026").nextBillingDate(date("March 1, 2026"))
        )
    }

    @Test
    fun billingTodayCountsAsUpcomingWithZeroDaysLeft() {
        val renewal = listOf(subscription(renewalDate = "October 20, 2026"))
            .upcomingRenewals(todayMillis = today)
            .single()

        assertEquals(today, renewal.dateMillis)
        assertEquals(0, renewal.daysLeft)
        assertEquals("Today", renewsInLabel(renewal.daysLeft))
    }

    @Test
    fun pausedAndCanceledSubscriptionsKeepTheirRecordedDate() {
        val paused = subscription(renewalDate = "September 1, 2026", status = SubscriptionStatus.PAUSED)

        assertEquals(date("September 1, 2026"), paused.nextBillingDate(today))
    }

    @Test
    fun unreadableDatesAreSkipped() {
        val broken = subscription(renewalDate = "Next week")

        assertNull(broken.nextBillingDate(today))
        assertEquals(emptyList<UpcomingRenewal>(), listOf(broken).upcomingRenewals(todayMillis = today))
    }

    @Test
    fun upcomingRenewalsAreActiveOnlySortedAndLimitedToTheWindow() {
        val subscriptions = listOf(
            subscription(name = "Netflix", renewalDate = "November 1, 2026", id = 1),
            subscription(name = "Spotify", renewalDate = "October 29, 2026", id = 2),
            subscription(name = "Gym", renewalDate = "October 22, 2026", id = 3),
            subscription(
                name = "Paused",
                renewalDate = "October 21, 2026",
                status = SubscriptionStatus.PAUSED,
                id = 4
            )
        )

        assertEquals(
            listOf("Gym", "Spotify", "Netflix"),
            subscriptions.upcomingRenewals(todayMillis = today).map { it.subscription.name }
        )
        assertEquals(
            listOf(2, 9, 12),
            subscriptions.upcomingRenewals(todayMillis = today).map { it.daysLeft }
        )
        assertEquals(
            listOf("Gym"),
            subscriptions
                .upcomingRenewals(withinDays = RenewalReminderDays, todayMillis = today)
                .map { it.subscription.name }
        )
    }

    @Test
    fun countdownLabelUsesSingularAndPlural() {
        assertEquals("in 1 day", renewsInLabel(1))
        assertEquals("in 12 days", renewsInLabel(12))
    }

    @Test
    fun monthHeadingsNameThisMonthNextMonthAndLaterMonths() {
        assertEquals("This Month", renewalMonthLabel(today, today))
        assertEquals("This Month", renewalMonthLabel(date("October 31, 2026"), today))
        assertEquals("Next Month", renewalMonthLabel(date("November 1, 2026"), today))
        assertEquals("December 2026", renewalMonthLabel(date("December 15, 2026"), today))
        assertEquals("October 2027", renewalMonthLabel(date("October 5, 2027"), today))
    }

    @Test
    fun nextMonthHeadingCrossesTheYearBoundary() {
        val december = date("December 20, 2026")

        assertEquals("Next Month", renewalMonthLabel(date("January 5, 2027"), december))
        assertEquals("February 2027", renewalMonthLabel(date("February 1, 2027"), december))
    }

    @Test
    fun reminderKeyChangesWhenTheBillingDateMovesToTheNextCycle() {
        val monthly = listOf(subscription(renewalDate = "October 25, 2026"))

        val beforeRenewal = monthly.upcomingRenewals(todayMillis = today).single()
        val afterRenewal = monthly.upcomingRenewals(todayMillis = date("October 26, 2026")).single()

        assertEquals(date("November 25, 2026"), afterRenewal.dateMillis)
        assertNotEquals(beforeRenewal.reminderKey, afterRenewal.reminderKey)
    }
}
