package com.subybara.app

import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso

/** Follow simulated account access rather than bypassing Welcome. */
fun ComposeContentTestRule.signInForTest() {
    // Existing workflow tests stage their fixtures before opening the account.
    val fixtures = com.subybara.app.core.data.SubscriptionStore.subscriptions.toList()
    onNodeWithText("I already have an account").performClick()
    onAllNodes(hasSetTextAction())[0].performTextInput("test@subybara.com")
    onAllNodes(hasSetTextAction())[1].performTextInput("secure123")
    Espresso.closeSoftKeyboard()
    onNodeWithText("Log In").performScrollTo().performClick()
    runOnIdle {
        val store = com.subybara.app.core.data.SubscriptionStore
        store.subscriptions.toList().forEach { store.remove(it.id) }
        fixtures.forEachIndexed { index, item -> store.restore(item, index) }
    }
}
