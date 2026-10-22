package com.subybara.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.SubscriptionCategory
import com.subybara.app.core.model.SubscriptionStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

/**
 * Removes a subscription in the real app and checks that Undo puts it back.
 */
class DeleteUndoTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun undoRestoresTheRemovedSubscriptionAtItsPosition() {
        var testId = 0
        var position = -1
        composeRule.runOnIdle {
            SubscriptionStore.add(
                Subscription(
                    name = "Undo Test Record",
                    amount = 99.0,
                    billingCycle = "Monthly",
                    renewalDate = "December 1, 2026",
                    category = SubscriptionCategory.OTHERS,
                    status = SubscriptionStatus.ACTIVE
                )
            )
            testId = SubscriptionStore.subscriptions.last().id
            position = SubscriptionStore.subscriptions.lastIndex
        }

        try {
            composeRule.signInForTest()
            composeRule.onNodeWithText("Subscriptions").performClick()
            composeRule.onNodeWithText("Undo Test Record").performScrollTo().performClick()
            composeRule.onNodeWithText("Remove Subscription").performScrollTo().performClick()
            composeRule.onAllNodesWithText("Remove Subscription").onLast().performClick()

            composeRule.onNodeWithText("Subscription deleted").assertIsDisplayed()
            composeRule.runOnIdle {
                assertNull(SubscriptionStore.findById(testId))
            }

            composeRule.onNodeWithText("Undo").performClick()

            composeRule.onNodeWithText("Subscription restored").assertIsDisplayed()
            composeRule.onNodeWithText("Undo Test Record").assertExists()
            composeRule.runOnIdle {
                assertNotNull(SubscriptionStore.findById(testId))
                assertEquals(
                    position,
                    SubscriptionStore.subscriptions.indexOfFirst { it.id == testId }
                )
            }
        } finally {
            composeRule.runOnIdle { SubscriptionStore.remove(testId) }
        }
    }
}
