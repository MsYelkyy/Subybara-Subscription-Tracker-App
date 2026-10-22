package com.subybara.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import org.junit.Rule
import org.junit.Test

class WelcomeNavigationTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun getStartedOpensSignUpAndBackReturnsToWelcome() {
        compose.onNodeWithText("Get Started").performClick()
        compose.onNodeWithText("Sign Up").performScrollTo().assertIsDisplayed()
        Espresso.pressBack()
        compose.onNodeWithText("Get Started").assertIsDisplayed()
    }

    @Test fun existingAccountOpensSignInAndForgotPasswordReturnsToIt() {
        compose.onNodeWithText("I already have an account").performClick()
        compose.onNodeWithText("Welcome back!").assertIsDisplayed()
        compose.onNodeWithText("Forgot password?").performScrollTo().performClick()
        Espresso.pressBack()
        compose.onNodeWithText("Welcome back!").assertIsDisplayed()
        Espresso.pressBack()
        compose.onNodeWithText("Get Started").assertIsDisplayed()
    }

    @Test fun signInReachesDashboardAndLogoutSurvivesRecreationAndBack() {
        compose.signInForTest()
        compose.onNodeWithText("Good Morning!").assertIsDisplayed()
        compose.onNodeWithText("Profile").performClick()
        compose.onNodeWithText("Log out").performScrollTo().performClick()
        compose.onNodeWithText("Get Started").assertIsDisplayed()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Get Started").assertIsDisplayed()
        compose.onNodeWithText("Good Morning!").assertDoesNotExist()
        val activity = compose.activity
        Espresso.pressBackUnconditionally()
        // Welcome lets system Back leave the app, never returning to Dashboard.
        compose.waitUntil(5_000) {
            activity.isFinishing || activity.lifecycle.currentState != Lifecycle.State.RESUMED
        }
    }
}
