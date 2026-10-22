package com.subybara.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import com.subybara.app.core.data.AccountStore
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.feature.profile.ProfileScreen
import com.subybara.app.navigation.SubybaraNavHost
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AccountNavigationAndRotationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        AccountStore.reset()
    }

    @Test
    fun signUp_savesUserAndDisplaysOnProfileScreen() {
        composeTestRule.setContent {
            SubybaraTheme {
                SubybaraNavHost()
            }
        }

        composeTestRule.onNodeWithText("Get Started").performClick()

        // Fill out Sign Up form
        composeTestRule.onNodeWithText("Juan").performTextInput("Mary-Jane")
        composeTestRule.onNodeWithText("Dela Cruz").performTextInput("O'Connor")
        composeTestRule.onNodeWithText("you@example.com").performTextInput("mary.jane@subybara.technology")
        composeTestRule.onNodeWithText("Min. 6 characters").performTextInput("secure123")
        composeTestRule.onNodeWithText("Re-enter password").performTextInput("secure123")
        Espresso.closeSoftKeyboard()

        // Submit Sign Up
        composeTestRule.onNodeWithText("Sign Up").performScrollTo().performClick()

        // Success Dialog shown
        composeTestRule.onNodeWithText("Account Created!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Go to Dashboard").performClick()

        composeTestRule.onNodeWithText("Good Morning!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Profile").performClick()
        composeTestRule.onNodeWithText("Mary-Jane O'Connor").assertIsDisplayed()

        // Verify state saved in AccountStore
        composeTestRule.runOnIdle {
            assertEquals("Mary-Jane O'Connor", AccountStore.currentUser.name)
            assertEquals("mary.jane@subybara.technology", AccountStore.currentUser.email)
            assertEquals("M", AccountStore.currentUser.initial)
        }
    }

    @Test
    fun profileScreen_rendersDynamicAccountStoreDetails() {
        AccountStore.updateUser("Alex Morgan", "alex.morgan@subybara.online")

        composeTestRule.setContent {
            SubybaraTheme {
                ProfileScreen(
                    onDestinationClick = {},
                    onAddSubscriptionClick = {},
                    onSignOutClick = {},
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("User Avatar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Alex Morgan").assertIsDisplayed()
        composeTestRule.onNodeWithText("alex.morgan@subybara.online").assertIsDisplayed()
    }
}
