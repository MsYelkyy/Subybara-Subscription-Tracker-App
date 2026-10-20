package com.subybara.app

import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.MutableSnapshot
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.core.model.*
import com.subybara.app.feature.subscriptions.SubscriptionFilter
import com.subybara.app.feature.subscriptions.filterSubscriptions
import org.junit.Assert.*
import org.junit.Before
import org.junit.After
import org.junit.Test

class SubscriptionTest {
    private lateinit var snapshot: MutableSnapshot
    private val sample = Subscription(name = "Test Music", amount = 149.0,
        billingCycle = "Monthly", renewalDate = "October 16, 2026",
        category = SubscriptionCategory.MUSIC, status = SubscriptionStatus.ACTIVE)

    // Keep test changes separate from shared data.
    @Before fun setup() { snapshot = Snapshot.takeMutableSnapshot() }
    @After fun cleanup() { snapshot.dispose() }
    private fun isolated(block: () -> Unit) = snapshot.enter {
        SubscriptionStore.subscriptions.toList().forEach { SubscriptionStore.remove(it.id) }
        block()
    }

    // Test 1: Add and find subscriptions.
    @Test fun addAndFind() = isolated {
        SubscriptionStore.add(sample)
        val saved = SubscriptionStore.subscriptions.single()
        assertTrue(saved.id > 0)
        assertEquals(sample.copy(id = saved.id), SubscriptionStore.findById(saved.id))
        SubscriptionStore.add(sample.copy(name = "Second"))
        assertEquals(2, SubscriptionStore.subscriptions.map { it.id }.distinct().size)
    }
    // Test 2: Keep the subscription ID when editing.
    @Test fun editPreservesIdentity() = isolated {
        SubscriptionStore.add(sample)
        val saved = SubscriptionStore.subscriptions.single()
        assertTrue(SubscriptionStore.update(saved.copy(name = "Updated", amount = 299.0)))
        assertEquals(saved.copy(name = "Updated", amount = 299.0), SubscriptionStore.findById(saved.id))
    }
    // Test 3: Remove subscriptions and handle missing IDs.
    @Test fun removeAndMissingIds() = isolated {
        SubscriptionStore.add(sample)
        val id = SubscriptionStore.subscriptions.single().id
        assertTrue(SubscriptionStore.remove(id))
        assertNull(SubscriptionStore.findById(id))
        assertFalse(SubscriptionStore.remove(id))
        assertFalse(SubscriptionStore.update(sample.copy(id = 999)))
        assertFalse(SubscriptionStore.changeStatus(999, SubscriptionStatus.PAUSED))
        assertTrue(SubscriptionStore.subscriptions.isEmpty())
    }
    @Test fun restorePutsRemovedSubscriptionBackInPlaceOnlyOnce() = isolated {
        listOf("First", "Second", "Third").forEach { SubscriptionStore.add(sample.copy(name = it)) }
        val second = SubscriptionStore.subscriptions[1]
        assertTrue(SubscriptionStore.remove(second.id))
        SubscriptionStore.restore(second, 1)
        assertEquals(listOf("First", "Second", "Third"), SubscriptionStore.subscriptions.map { it.name })
        SubscriptionStore.restore(second, 1)
        assertEquals(3, SubscriptionStore.subscriptions.size)
    }
    @Test fun remindersFollowAddEditPauseResumeAndCancel() = isolated {
        val today = parseDate("October 20, 2026")!!
        fun reminders() = SubscriptionStore.subscriptions
            .upcomingRenewals(withinDays = RenewalReminderDays, todayMillis = today)
        fun edit(change: (Subscription) -> Subscription) =
            assertTrue(SubscriptionStore.update(change(SubscriptionStore.subscriptions.single())))
        fun status(status: SubscriptionStatus) =
            assertTrue(SubscriptionStore.changeStatus(SubscriptionStore.subscriptions.single().id, status))
        SubscriptionStore.add(sample.copy(renewalDate = "October 22, 2026"))
        val added = reminders().single()
        assertEquals(2, added.daysLeft)
        edit { it.copy(name = "Renamed", amount = 199.0) }
        assertEquals("Renamed", reminders().single().subscription.name)
        assertEquals(added.reminderKey, reminders().single().reminderKey)
        edit { it.copy(renewalDate = "November 15, 2026") }
        assertTrue(reminders().isEmpty())
        edit { it.copy(renewalDate = "October 25, 2026") }
        val moved = reminders().single()
        assertEquals(5, moved.daysLeft)
        assertNotEquals(added.reminderKey, moved.reminderKey)
        status(SubscriptionStatus.PAUSED)
        assertTrue(reminders().isEmpty())
        status(SubscriptionStatus.ACTIVE)
        assertEquals(moved.reminderKey, reminders().single().reminderKey)
        status(SubscriptionStatus.CANCELED)
        assertTrue(reminders().isEmpty())
        SubscriptionStore.remove(SubscriptionStore.subscriptions.single().id)
        assertTrue(reminders().isEmpty())
    }
    // Test 4: Change status without removing the subscription.
    @Test fun allStatusTransitionsKeepRecord() = isolated {
        SubscriptionStore.add(sample)
        val saved = SubscriptionStore.subscriptions.single()
        listOf(SubscriptionStatus.PAUSED, SubscriptionStatus.ACTIVE,
            SubscriptionStatus.CANCELED, SubscriptionStatus.ACTIVE).forEach { status ->
            assertTrue(SubscriptionStore.changeStatus(saved.id, status))
            assertEquals(listOf(saved.copy(status = status)), SubscriptionStore.subscriptions)
        }
    }
    // Test 5: Filter and search subscriptions.
    @Test fun filtersAndSearch() {
        val records = listOf(sample, sample.copy(id = 2, name = "Video", status = SubscriptionStatus.PAUSED),
            sample.copy(id = 3, name = "Music Plus", status = SubscriptionStatus.CANCELED))
        assertEquals(records, filterSubscriptions(records, "", SubscriptionFilter.All))
        assertEquals(listOf(records[0]), filterSubscriptions(records, "", SubscriptionFilter.Active))
        assertEquals(listOf(records[1]), filterSubscriptions(records, "", SubscriptionFilter.Paused))
        assertEquals(listOf(records[2]), filterSubscriptions(records, "", SubscriptionFilter.Canceled))
        assertEquals(listOf(records[2]), filterSubscriptions(records, " MUSIC ", SubscriptionFilter.Canceled))
        assertTrue(filterSubscriptions(records, "music", SubscriptionFilter.Paused).isEmpty())
        assertTrue(filterSubscriptions(emptyList(), "", SubscriptionFilter.All).isEmpty())
    }
    // Test 6: Format amounts in pesos.
    @Test fun amountFormattingIsConsistent() {
        assertEquals("\u20B11,234.50", formatSubscriptionAmount(1234.5))
    }
    // Test 7: Reject dates with extra text.
    @Test fun dateParsingRejectsTrailingText() {
        val invalidDate = "October 16, 2026 invalid"
        assertNull(parseDate(invalidDate))
        assertEquals(invalidDate, reformatDate(invalidDate, "MMM d, yyyy"))
    }

    // Test 8: Calculate days across month and year boundaries.
    @Test fun datesAcrossBoundaries() {
        fun days(date: String, today: String) = daysUntil(parseDate(date)!!, parseDate(today)!!)
        assertEquals(0, days("October 16, 2026", "October 16, 2026"))
        assertEquals(-1, days("October 15, 2026", "October 16, 2026"))
        assertEquals(1, days("January 1, 2027", "December 31, 2026"))
        assertEquals(1, days("March 1, 2026", "February 28, 2026"))
        assertEquals(2, days("March 1, 2024", "February 28, 2024"))
        assertNull(parseDate("invalid"))
        assertNull(parseDate("February 30, 2026"))
        assertEquals("October 16, 2026", formatDate(parseDate("October 16, 2026")!!))
    }
}
