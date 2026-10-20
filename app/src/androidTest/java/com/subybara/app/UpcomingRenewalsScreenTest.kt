package com.subybara.app

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.SubscriptionCategory
import com.subybara.app.core.model.SubscriptionStatus
import com.subybara.app.core.model.addBillingCycles
import com.subybara.app.core.model.formatDate
import com.subybara.app.core.model.startOfMonth
import com.subybara.app.core.model.todayInMillis
import com.subybara.app.feature.renewals.UpcomingRenewalsScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Checks that the Renewals list shows each renewal under the heading for the
 * month it renews in.
 */
class UpcomingRenewalsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun renewalsAreListedUnderTheirOwnMonthHeading() {
        val today = todayInMillis()
        val nextMonth = addBillingCycles(startOfMonth(today), "Monthly", 1)
        val monthAfterNext = addBillingCycles(startOfMonth(today), "Monthly", 2)
        val laterHeading = formatDate(monthAfterNext, "MMMM yyyy")

        composeRule.setContent {
            SubybaraTheme {
                UpcomingRenewalsScreen(
                    subscriptions = listOf(
                        subscription(id = 1, name = "Renews Today", dateMillis = today),
                        subscription(id = 2, name = "Renews Next Month", dateMillis = nextMonth),
                        subscription(id = 3, name = "Renews Later", dateMillis = monthAfterNext)
                    )
                )
            }
        }
        composeRule.onNodeWithText("All Upcoming").performClick()

        val tops = listOf(
            "This Month",
            "Renews Today",
            "Next Month",
            "Renews Next Month",
            laterHeading,
            "Renews Later"
        ).map { text -> composeRule.onNodeWithText(text).assertIsDisplayed().getBoundsInRoot().top }

        assertEquals(tops.sorted(), tops)
        composeRule.onAllNodesWithText("This Month").assertCountEquals(1)
        listOf("This Month", "Next Month", laterHeading).forEach { heading ->
            composeRule.onNodeWithText(heading)
                .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
        }
    }

    @Test
    fun emptyListShowsTheMessageWithoutAMonthHeading() {
        composeRule.setContent {
            SubybaraTheme {
                UpcomingRenewalsScreen(subscriptions = emptyList())
            }
        }

        composeRule.onNodeWithText("No upcoming renewals found.").assertIsDisplayed()
        composeRule.onNodeWithText("This Month").assertDoesNotExist()
    }

    private fun subscription(id: Int, name: String, dateMillis: Long) = Subscription(
        id = id,
        name = name,
        amount = 149.0,
        billingCycle = "Monthly",
        renewalDate = formatDate(dateMillis),
        category = SubscriptionCategory.MUSIC,
        status = SubscriptionStatus.ACTIVE
    )
}
