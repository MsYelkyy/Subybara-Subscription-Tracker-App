package com.subybara.app

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import com.subybara.app.core.data.SubscriptionStore
import org.junit.Before
import org.junit.After
import com.subybara.app.core.model.*
import org.junit.Rule
import org.junit.Test

/**
 * Runs the real app, rotates the screen, and presses the system Back button
 * to check that state and navigation stay stable.
 */
class RotationAndBackNavigationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private var fixtureId = 0

    @Before fun addFixture() {
        composeRule.runOnIdle {
            SubscriptionStore.add(Subscription(name = "Rotation fixture", amount = 100.0,
                billingCycle = "Monthly", renewalDate = formatDate(todayInMillis()),
                category = SubscriptionCategory.MUSIC, status = SubscriptionStatus.ACTIVE,
                paymentMethod = "GCash"))
            fixtureId = SubscriptionStore.subscriptions.last().id
        }
    }

    @After fun removeFixture() {
        composeRule.runOnIdle { SubscriptionStore.remove(fixtureId) }
    }

    @Test
    fun signUpFormKeepsInputAfterRotation() {
        composeRule.onNodeWithText("I already have an account").performClick()
        composeRule.onNodeWithText("Don't have an account? Sign up").performClick()
        composeRule.onNodeWithText("Juan").performTextInput("Ana")
        composeRule.onNodeWithText("you@example.com").performTextInput("ana@subybara.com")

        rotateTo(landscape = true)

        composeRule.onNodeWithText("Ana").assertExists()
        composeRule.onNodeWithText("ana@subybara.com").assertExists()
        composeRule.onNodeWithText("Sign Up").performScrollTo().assertIsDisplayed()

        rotateTo(landscape = false)

        composeRule.onNodeWithText("Ana").assertExists()
    }

    @Test
    fun subscriptionDetailsStayOpenAfterRotationAndBackReturnsToList() {
        val name = requireNotNull(SubscriptionStore.findById(fixtureId)).name
        composeRule.signInForTest()
        composeRule.onNodeWithText("Subscriptions").performClick()
        composeRule.onNodeWithText(name).performClick()

        rotateTo(landscape = true)

        composeRule.onNodeWithText(name).assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Edit subscription").assertExists()

        rotateTo(landscape = false)
        Espresso.pressBack()

        composeRule.onNodeWithText("My Subscription").assertIsDisplayed()
    }

    @Test
    fun editSaveDialogStaysOpenAfterRotation() {
        val name = requireNotNull(SubscriptionStore.findById(fixtureId)).name
        composeRule.signInForTest()
        composeRule.onNodeWithText("Subscriptions").performClick()
        composeRule.onNodeWithText(name).performClick()
        composeRule.onNodeWithContentDescription("Edit subscription").performClick()
        composeRule.onNodeWithContentDescription("Subscription Name").performTextInput(" Plus")
        composeRule.onNodeWithText("Save Subscription").performScrollTo().performClick()
        composeRule.onNodeWithText("Save changes?").assertIsDisplayed()

        rotateTo(landscape = true)

        composeRule.onNodeWithText("Save changes?").assertIsDisplayed()
        composeRule.onNodeWithText("No").performClick()
        composeRule.onNodeWithText("Save changes?").assertDoesNotExist()

        rotateTo(landscape = false)
    }

    @Test
    fun backFromSignUpAndSignInReturnsToPreviousScreen() {
        composeRule.onNodeWithText("I already have an account").performClick()
        composeRule.onNodeWithText("Don't have an account? Sign up").performClick()

        Espresso.pressBack()
        composeRule.onNodeWithText("Welcome back!").assertIsDisplayed()

        Espresso.pressBack()
        composeRule.onNodeWithText("Get Started").assertIsDisplayed()
    }

    @Test
    fun backFromBottomBarTabReturnsToHome() {
        composeRule.signInForTest()
        composeRule.onNodeWithText("Profile").performClick()

        Espresso.pressBack()

        composeRule.onNodeWithText("Good Morning!").assertIsDisplayed()
    }

    private fun rotateTo(landscape: Boolean) {
        val requested = if (landscape) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        val expected = if (landscape) {
            Configuration.ORIENTATION_LANDSCAPE
        } else {
            Configuration.ORIENTATION_PORTRAIT
        }

        composeRule.activityRule.scenario.onActivity { activity ->
            activity.requestedOrientation = requested
        }
        composeRule.waitUntil(timeoutMillis = 5_000) {
            var current = Configuration.ORIENTATION_UNDEFINED
            composeRule.activityRule.scenario.onActivity { activity ->
                current = activity.resources.configuration.orientation
            }
            current == expected
        }
        composeRule.waitForIdle()
    }
}
