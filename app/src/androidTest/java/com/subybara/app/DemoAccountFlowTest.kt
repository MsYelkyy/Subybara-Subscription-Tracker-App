package com.subybara.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.graphics.asAndroidBitmap
import android.graphics.Bitmap
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import androidx.test.espresso.Espresso
import com.subybara.app.core.data.AccountStore
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.navigation.SubybaraNavHost
import org.junit.*
import org.junit.Assert.*

class DemoAccountFlowTest {
    @get:Rule val compose = createComposeRule()
    @Before fun before() {
        AccountStore.reset()
        compose.setContent { SubybaraTheme { SubybaraNavHost() } }
    }
    @After fun after() { compose.runOnIdle { AccountStore.reset() } }

    private fun login(password: String = AccountStore.DemoPassword) {
        compose.onNodeWithText("I already have an account").performClick()
        compose.onAllNodes(hasSetTextAction())[0].performTextInput(AccountStore.DemoEmail)
        compose.onAllNodes(hasSetTextAction())[1].performTextInput(password)
        Espresso.closeSoftKeyboard()
        compose.onNodeWithText("Log In").performScrollTo().performClick()
    }
    private fun logout() {
        compose.onNodeWithText("Profile").performClick()
        compose.onNodeWithText("Log out").performScrollTo().performClick()
        compose.onNodeWithText("Get Started").assertIsDisplayed()
    }

    @Test fun demoSignupAndSwitchingShowTheCorrectDataAndEmptyState() {
        login()
        compose.onNodeWithContentDescription("Notifications, new renewal reminders").performClick()
        compose.onNodeWithText("Netflix renews today").assertIsDisplayed()
        Espresso.pressBack()
        compose.onNodeWithTag("projection-line").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Insights").performClick()
        compose.onNodeWithTag("projection-bar").assertIsDisplayed()
        logout()
        compose.onNodeWithText("Get Started").performClick()
        compose.onNodeWithText("Juan").performTextInput("Ana")
        compose.onNodeWithText("Dela Cruz").performTextInput("Cruz")
        compose.onNodeWithText("you@example.com").performTextInput("ana@example.com")
        compose.onNodeWithText("Min. 6 characters").performTextInput("secure123")
        compose.onNodeWithText("Re-enter password").performTextInput("secure123")
        Espresso.closeSoftKeyboard()
        compose.onNodeWithText("Sign Up").performScrollTo().performClick()
        compose.onNodeWithText("Go to Dashboard").performClick()
        compose.runOnIdle { assertTrue(SubscriptionStore.subscriptions.isEmpty()) }
        compose.onNodeWithText("Tracking starts here").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Add your first subscription to see your projected monthly bills.").assertIsDisplayed()
        compose.onNodeWithTag("projection-line").assertDoesNotExist()
        val directory = File(InstrumentationRegistry.getInstrumentation().targetContext.cacheDir, "chart-previews")
        directory.mkdirs()
        File(directory, "home-empty.png").outputStream().use {
            compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        logout()
        login()
        compose.runOnIdle { assertEquals(7, SubscriptionStore.subscriptions.size) }
        compose.onNodeWithTag("projection-line").performScrollTo().assertIsDisplayed()
    }

    @Test fun wrongDemoPasswordDoesNotLoadSamples() {
        login("wrong")
        compose.onNodeWithText("Incorrect demo password. Please try again.").assertIsDisplayed()
        compose.runOnIdle { assertTrue(SubscriptionStore.subscriptions.isEmpty()) }
    }
}
