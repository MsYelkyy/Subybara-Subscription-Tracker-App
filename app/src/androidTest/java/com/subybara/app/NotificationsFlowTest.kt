package com.subybara.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.test.espresso.Espresso
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.SubscriptionCategory
import com.subybara.app.core.model.SubscriptionStatus
import com.subybara.app.core.model.formatDate
import com.subybara.app.core.model.todayInMillis
import org.junit.Rule
import org.junit.Test

/**
 * Runs the real app to check the notification bell, the reminder list, and
 * navigation between a reminder and its subscription details.
 */
class NotificationsFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun unreadReminderOpensDetailsAndIsMarkedAsSeen() {
        var testId = 0
        composeRule.runOnIdle { testId = addReminderRecord("Reminder Test Record", daysAhead = 2) }

        try {
            composeRule.onNodeWithText("Get Started").performClick()
            composeRule.onNodeWithContentDescription("Notifications, new renewal reminders").performClick()

            composeRule.onNodeWithText("Reminder Test Record renews in 2 days")
                .performScrollTo()
                .performClick()
            composeRule.onNodeWithContentDescription("Edit subscription").assertExists()
            composeRule.onNodeWithText("Reminder Test Record").assertIsDisplayed()

            Espresso.pressBack()
            composeRule.onNodeWithText("Reminder Test Record renews in 2 days").assertExists()

            Espresso.pressBack()
            composeRule.onNodeWithText("Good Morning!").assertIsDisplayed()
            composeRule.onNodeWithContentDescription("Notifications").assertIsDisplayed()
            composeRule.onNodeWithContentDescription("Notifications, new renewal reminders")
                .assertDoesNotExist()
        } finally {
            composeRule.runOnIdle { SubscriptionStore.remove(testId) }
        }
    }

    @Test
    fun pausingOrCancelingRemovesTheReminderAndResumingBringsItBackAsSeen() {
        var testId = 0
        composeRule.runOnIdle { testId = addReminderRecord("Status Reminder Record", daysAhead = 2) }

        try {
            composeRule.onNodeWithText("Get Started").performClick()
            openNotifications()
            composeRule.onNodeWithText("Status Reminder Record renews in 2 days")
                .performScrollTo()
                .performClick()
            composeRule.onNodeWithText("Pause Subscription").performScrollTo().performClick()

            Espresso.pressBack()
            composeRule.onNodeWithText("Status Reminder Record", substring = true).assertDoesNotExist()

            Espresso.pressBack()
            waitForMessageToClose("Tracking status updated")
            openNotifications()
            Espresso.pressBack()

            composeRule.onNodeWithText("Subscriptions").performClick()
            composeRule.onNodeWithText("Status Reminder Record").performScrollTo().performClick()
            composeRule.onNodeWithText("Resume Subscription").performScrollTo().performClick()

            Espresso.pressBack()
            composeRule.onNodeWithText("Home").performClick()
            composeRule.onNodeWithContentDescription("Notifications").assertIsDisplayed()

            waitForMessageToClose("Tracking status updated")
            openNotifications()
            composeRule.onNodeWithText("Status Reminder Record renews in 2 days")
                .performScrollTo()
                .performClick()
            composeRule.onNodeWithText("Mark as Canceled").performScrollTo().performClick()

            Espresso.pressBack()
            composeRule.onNodeWithText("Status Reminder Record", substring = true).assertDoesNotExist()
        } finally {
            composeRule.runOnIdle { SubscriptionStore.remove(testId) }
        }
    }

    @Test
    fun editingTheDateOrNameUpdatesTheReminder() {
        var testId = 0
        composeRule.runOnIdle { testId = addReminderRecord("Edit Reminder Record", daysAhead = 20) }

        try {
            composeRule.onNodeWithText("Get Started").performClick()
            openNotifications()
            composeRule.onNodeWithText("Edit Reminder Record", substring = true).assertDoesNotExist()

            Espresso.pressBack()
            composeRule.runOnIdle { moveRenewal(testId, daysAhead = 3) }
            composeRule.onNodeWithContentDescription("Notifications, new renewal reminders").performClick()
            composeRule.onNodeWithText("Edit Reminder Record renews in 3 days")
                .performScrollTo()
                .performClick()

            composeRule.onNodeWithContentDescription("Edit subscription").performClick()
            composeRule.onNodeWithContentDescription("Subscription Name")
                .performTextReplacement("Renamed Reminder Record")
            Espresso.closeSoftKeyboard()
            composeRule.onNodeWithText("Save Subscription").performScrollTo().performClick()
            composeRule.onNodeWithText("Yes").performClick()

            Espresso.pressBack()
            composeRule.onNodeWithText("Renamed Reminder Record renews in 3 days").assertExists()

            Espresso.pressBack()
            composeRule.onNodeWithContentDescription("Notifications").assertIsDisplayed()

            composeRule.runOnIdle { moveRenewal(testId, daysAhead = 30) }
            waitForMessageToClose("Changes saved!")
            openNotifications()
            composeRule.onNodeWithText("Renamed Reminder Record", substring = true).assertDoesNotExist()
        } finally {
            composeRule.runOnIdle { SubscriptionStore.remove(testId) }
        }
    }

    private fun addReminderRecord(name: String, daysAhead: Int): Int {
        SubscriptionStore.add(
            Subscription(
                name = name,
                amount = 120.0,
                billingCycle = "Monthly",
                renewalDate = formatDate(todayInMillis() + daysAhead * DayMillis),
                category = SubscriptionCategory.OTHERS,
                status = SubscriptionStatus.ACTIVE,
                paymentMethod = "GCash"
            )
        )
        return SubscriptionStore.subscriptions.last().id
    }

    private fun moveRenewal(id: Int, daysAhead: Int) {
        val subscription = requireNotNull(SubscriptionStore.findById(id))
        SubscriptionStore.update(
            subscription.copy(renewalDate = formatDate(todayInMillis() + daysAhead * DayMillis))
        )
    }

    private fun openNotifications() {
        composeRule.onNode(hasContentDescription("Notifications", substring = true)).performClick()
    }

    private fun waitForMessageToClose(message: String) {
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText(message).fetchSemanticsNodes().isEmpty()
        }
    }

    private companion object {
        const val DayMillis = 24L * 60L * 60L * 1000L
    }
}
