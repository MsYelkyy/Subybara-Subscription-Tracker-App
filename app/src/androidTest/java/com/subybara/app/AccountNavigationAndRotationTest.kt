package com.subybara.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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

        // Navigate from Welcome screen to Sign In
        composeTestRule.onNodeWithText("I already have an account").performClick()

        // Navigate from Sign In to Sign Up
        composeTestRule.onNodeWithText("Don't have an account? Sign up").performClick()

        // Fill out Sign Up form
        composeTestRule.onNodeWithText("Full name").performTextInput("Mary-Jane O'Connor")
        composeTestRule.onNodeWithText("Email address").performTextInput("mary.jane@subybara.technology")
        composeTestRule.onNodeWithText("Password").performTextInput("secure123")

        // Submit Sign Up
        composeTestRule.onNodeWithText("Sign Up").performClick()

        // Success Dialog shown
        composeTestRule.onNodeWithText("Account Created!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Proceed to Sign In").performClick()

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

        composeTestRule.onNodeWithText("A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Alex Morgan").assertIsDisplayed()
        composeTestRule.onNodeWithText("alex.morgan@subybara.online").assertIsDisplayed()
    }
}
