package com.subybara.app

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.Espresso
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.model.*
import com.subybara.app.feature.subscriptions.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class SubscriptionWorkflowTest {
    @get:Rule val compose = createComposeRule()
    private val sample = Subscription(id = 1, name = "Test Music", amount = 149.0,
        billingCycle = "Monthly", renewalDate = "October 16, 2026",
        category = SubscriptionCategory.MUSIC, status = SubscriptionStatus.ACTIVE,
        paymentMethod = "GCash")

    private fun save() {
        Espresso.closeSoftKeyboard()
        compose.onNodeWithText("Save Subscription").performScrollTo().performClick()
    }

    // Test 1: Add a valid subscription.
    @Test fun addValidSubscription() {
        var saved: Subscription? = null
        compose.setContent { SubybaraTheme {
            AddSubscriptionScreen(onCloseClick = {}, onSaveClick = { saved = it }, onDoneClick = {})
        } }
        compose.onNodeWithContentDescription("Subscription Name").performTextInput("Study Plan")
        compose.onNodeWithText("Select Category").performScrollTo().performClick()
        compose.onNodeWithText("Education").performClick()
        compose.onNodeWithContentDescription("Billing Amount").performScrollTo().performTextInput("99.50")
        compose.onNodeWithText("Select Payment Method").performScrollTo().performClick()
        compose.onNodeWithText("GCash").performClick()
        save()
        compose.onNodeWithText("Added successfully!").assertExists()
        compose.runOnIdle {
            assertEquals("Study Plan", saved?.name)
            assertEquals(99.5, saved!!.amount, 0.0)
        }
    }

    // Test 2: Prevent saving invalid inputs.
    @Test fun invalidInputsNeverSave() {
        var saves = 0
        compose.setContent { SubybaraTheme {
            EditSubscriptionScreen(sample, {}, { saves++ })
        } }
        compose.onNodeWithContentDescription("Subscription Name").performTextReplacement("   ")
        save()
        compose.onNodeWithText("Enter a subscription name").assertExists()
        compose.onNode(SemanticsMatcher.expectValue(
            SemanticsProperties.Error, "Enter a subscription name"
        )).assertExists()
        compose.onNodeWithContentDescription("Subscription Name").performScrollTo().performTextReplacement("Valid")
        listOf("0", "-1", "invalid", "").forEach { amount ->
            compose.onNodeWithContentDescription("Billing Amount").performScrollTo().performTextClearance()
            if (amount.isNotEmpty()) compose.onNodeWithContentDescription("Billing Amount").performTextInput(amount)
            save()
            compose.onNodeWithText("Enter an amount greater than 0").assertExists()
            compose.onNodeWithText("Save changes?").assertDoesNotExist()
        }
        compose.runOnIdle { assertEquals(0, saves) }
    }

    // Test 3: Confirm changes before saving.
    @Test fun editRequiresConfirmation() {
        var saved = sample
        compose.setContent { SubybaraTheme { EditSubscriptionScreen(sample, {}, { saved = it }) } }
        compose.onNodeWithContentDescription("Subscription Name").performTextReplacement("Updated Music")
        save()
        compose.runOnIdle { assertEquals(sample, saved) }
        compose.onNodeWithText("Yes").performClick()
        compose.runOnIdle { assertEquals(sample.copy(name = "Updated Music"), saved) }
    }

    // Test 4: Discard changes without saving.
    @Test fun discardDoesNotSave() {
        var saved = sample
        var closed = false
        compose.setContent { SubybaraTheme { EditSubscriptionScreen(sample, { closed = true }, { saved = it }) } }
        compose.onNodeWithContentDescription("Subscription Name").performTextReplacement("Unsaved")
        compose.onNodeWithContentDescription("Close").performClick()
        compose.onNodeWithText("Keep Editing").performClick()
        compose.runOnIdle { assertFalse(closed) }
        compose.onNodeWithContentDescription("Close").performClick()
        compose.onNodeWithText("Discard Changes").performClick()
        compose.runOnIdle { assertTrue(closed); assertEquals(sample, saved) }
    }

    // Test 5: Change status and confirm removal.
    @Test fun detailsStatusAndRemoval() {
        val saved = mutableStateOf(sample)
        var removals = 0
        compose.setContent { SubybaraTheme {
            SubscriptionDetailsScreen(saved.value, {}, {}, { removals++ },
                { saved.value = saved.value.copy(status = it) })
        } }
        fun action(label: String) = compose.onNodeWithText(label).performScrollTo().performClick()
        action("Pause Subscription")
        compose.onNodeWithText("Paused").assertExists()
        action("Resume Subscription")
        compose.onNodeWithText("Active").assertExists()
        action("Mark as Canceled")
        compose.onNodeWithText("Canceled").assertExists()
        compose.onNodeWithText("Pause Subscription").assertDoesNotExist()
        action("Mark as Active")
        action("Remove Subscription")
        compose.onNodeWithText("Keep Subscription").performClick()
        compose.runOnIdle { assertEquals(0, removals) }
        action("Remove Subscription")
        // Select the dialog button, not the screen action.
        compose.onAllNodesWithText("Remove Subscription").onLast().performClick()
        compose.runOnIdle { assertEquals(1, removals) }
    }

    // Test 6: Update shared data and return to the list after removal.
    @Test fun navigationUpdatesSharedStoreAndReturnsAfterRemoval() {
        val store = com.subybara.app.core.data.SubscriptionStore
        var testId = 0
        compose.setContent { SubybaraTheme { com.subybara.app.navigation.SubybaraNavHost() } }
        try {
            compose.runOnIdle {
                store.add(sample.copy(name = "Workflow Test Record"))
                testId = store.subscriptions.last().id
            }
            compose.signInForTest()
            compose.onNodeWithText("Subscriptions").performClick()
            compose.onNodeWithText("Workflow Test Record").performScrollTo().performClick()
            compose.onNodeWithText("Pause Subscription").performScrollTo().performClick()
            compose.runOnIdle { assertEquals(SubscriptionStatus.PAUSED, store.findById(testId)?.status) }
            compose.onNodeWithContentDescription("Back").performClick()
            compose.onNodeWithText("Paused").performClick()
            compose.onNodeWithText("Workflow Test Record").performScrollTo().performClick()
            compose.onNodeWithContentDescription("Edit subscription").performClick()
            compose.onNodeWithContentDescription("Subscription Name").performTextReplacement("Edited Workflow Record")
            save()
            compose.onNodeWithText("Yes").performClick()
            compose.onNodeWithText("Edited Workflow Record").assertExists()
            compose.runOnIdle { assertEquals("Edited Workflow Record", store.findById(testId)?.name) }
            compose.onNodeWithText("Remove Subscription").performScrollTo().performClick()
            compose.onAllNodesWithText("Remove Subscription").onLast().performClick()
            val expectedHeading = if (store.subscriptions.isEmpty()) "No subscriptions yet" else "My Subscription"
            compose.onNodeWithText(expectedHeading).assertExists()
            compose.runOnIdle { assertNull(store.findById(testId)) }
        } finally {
            // Remove only the record created by this test.
            compose.runOnIdle { if (testId != 0) store.remove(testId) }
        }
    }

    // Test 7: Clear search and status filters.
    @Test fun emptyResultsClearSearchAndStatus() {
        val records = mutableStateListOf(sample)
        compose.setContent { SubybaraTheme { SubscriptionsScreen(records, {}, {}, {}) } }
        compose.onNodeWithText("Paused").performClick()
        compose.onNodeWithContentDescription("Search subscriptions").performTextInput("missing")
        compose.onNodeWithText("No matching subscriptions").assertExists()
        compose.onNodeWithText("Clear Filters").performClick()
        compose.onNodeWithText("Test Music").assertExists()
        compose.onNodeWithText("All (1)").assertExists()
        compose.onNodeWithText("No matching subscriptions").assertDoesNotExist()
        compose.runOnIdle { records.clear() }
        compose.onNodeWithText("No subscriptions yet").assertExists()
        compose.onNodeWithText("Clear Filters").assertDoesNotExist()
    }

    // Test 8: Show Clear Filters only when search has text.
    @Test fun clearFiltersOnlyAppearsWhenSearchHasText() {
        val records = mutableStateListOf(sample)
        compose.setContent { SubybaraTheme { SubscriptionsScreen(records, {}, {}, {}) } }
        compose.onNodeWithText("Paused").performClick()
        compose.onNodeWithText("No matching subscriptions").assertExists()
        compose.onNodeWithText("Clear Filters").assertDoesNotExist()
        compose.onNodeWithContentDescription("Search subscriptions").performTextInput("missing")
        compose.onNodeWithText("Clear Filters").assertExists()
    }

    // Test 9: Center the empty search results message.
    @Test fun emptyResultsMessageIsCentered() {
        val records = mutableStateListOf(sample)
        compose.setContent { SubybaraTheme { SubscriptionsScreen(records, {}, {}, {}) } }
        compose.onNodeWithText("Paused").performClick()
        val root = compose.onRoot().getBoundsInRoot()
        val message = compose.onNodeWithText("No matching subscriptions").getBoundsInRoot()
        assertEquals(
            (root.left.value + root.right.value) / 2,
            (message.left.value + message.right.value) / 2,
            1f
        )
        val middle = (message.top.value + message.bottom.value) / 2
        val rootHeight = root.bottom.value - root.top.value
        assertTrue(middle > rootHeight * 0.3f && middle < rootHeight * 0.7f)
    }
}
