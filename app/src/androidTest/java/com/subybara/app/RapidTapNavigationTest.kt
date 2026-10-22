package com.subybara.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.Espresso
import com.subybara.app.core.data.AccountStore
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.navigation.SubybaraNavHost
import com.subybara.app.navigation.navigationCooldownMillis
import org.junit.*

class RapidTapNavigationTest {
    @get:Rule val compose = createComposeRule()

    @Before fun before() {
        AccountStore.reset()
        navigationCooldownMillis = 5_000L
        compose.setContent { SubybaraTheme { SubybaraNavHost() } }
    }

    @After fun after() {
        navigationCooldownMillis = 0L
        compose.runOnIdle { AccountStore.reset() }
    }

    @Test fun tapRightAfterAScreenChangeDoesNotNavigateAgainButBackStillWorks() {
        compose.onNodeWithText("Get Started").performClick()
        compose.onNodeWithText("Create account").assertIsDisplayed()
        compose.onNodeWithText("Log in", substring = true).performScrollTo().performClick()
        compose.onNodeWithText("Create account").assertIsDisplayed()
        compose.onNodeWithText("Welcome back!").assertDoesNotExist()
        Espresso.pressBack()
        compose.onNodeWithText("Get Started").assertIsDisplayed()
    }
}
