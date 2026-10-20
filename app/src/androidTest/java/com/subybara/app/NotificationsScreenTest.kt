package com.subybara.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.model.RenewalReminderDays
import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.SubscriptionCategory
import com.subybara.app.core.model.SubscriptionStatus
import com.subybara.app.core.model.parseDate
import com.subybara.app.core.model.upcomingRenewals
import com.subybara.app.feature.notifications.NotificationsScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class NotificationsScreenTest {

    @get:Rule
    val compose = createComposeRule()

    private val today = requireNotNull(parseDate("October 20, 2026"))

    private fun subscription(name: String, renewalDate: String, id: Int) = Subscription(
        id = id,
        name = name,
        amount = 149.0,
        billingCycle = "Monthly",
        renewalDate = renewalDate,
        category = SubscriptionCategory.MUSIC,
        status = SubscriptionStatus.ACTIVE
    )

    @Test
    fun emptyStateExplainsWhichRenewalsAppearHere() {
        compose.setContent {
            SubybaraTheme {
                NotificationsScreen(reminders = emptyList(), onReminderClick = {}, onBackClick = {})
            }
        }

        compose.onNodeWithText("No notifications yet").assertIsDisplayed()
        compose.onNodeWithText("Renewals due in the next 7 days will appear here.").assertIsDisplayed()
    }

    @Test
    fun remindersShowTimingAndOpenTheirSubscription() {
        val renewingSoon = subscription("Gym", "October 22, 2026", id = 1)
        val renewingToday = subscription("Spotify", "October 20, 2026", id = 2)
        val renewingLater = subscription("Netflix", "November 1, 2026", id = 3)
        val reminders = listOf(renewingLater, renewingSoon, renewingToday)
            .upcomingRenewals(withinDays = RenewalReminderDays, todayMillis = today)
        var opened: Subscription? = null

        compose.setContent {
            SubybaraTheme {
                NotificationsScreen(
                    reminders = reminders,
                    onReminderClick = { opened = it },
                    onBackClick = {}
                )
            }
        }

        compose.onNodeWithText("Spotify renews today").assertIsDisplayed()
        compose.onNodeWithText("Gym renews in 2 days").assertIsDisplayed()
        compose.onNodeWithText("Netflix", substring = true).assertDoesNotExist()

        compose.onNodeWithText("Gym renews in 2 days").performClick()
        compose.runOnIdle { assertEquals(renewingSoon, opened) }
    }
}
