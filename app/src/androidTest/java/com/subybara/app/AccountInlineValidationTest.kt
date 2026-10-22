package com.subybara.app

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.Espresso
import com.subybara.app.core.data.AccountStore
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.feature.account.SignInScreen
import com.subybara.app.feature.account.SignUpScreen
import org.junit.*
import org.junit.Assert.*

class AccountInlineValidationTest {
    @get:Rule val compose = createComposeRule()
    @Before fun before() = AccountStore.reset()
    @After fun after() = AccountStore.reset()

    private fun fieldWithError(message: String) =
        compose.onNode(SemanticsMatcher.expectValue(SemanticsProperties.Error, message))

    @Test fun signInShowsFieldErrorsOnSubmitAndClearsThemWhenCorrected() {
        var signedIn = false
        compose.setContent {
            SubybaraTheme { SignInScreen(onSignInSuccess = { signedIn = true }, onForgotPasswordClick = {}, onSignUpClick = {}) }
        }
        compose.onNodeWithText("Please enter your email address.").assertDoesNotExist()
        compose.onNodeWithText("Log In").performScrollTo().performClick()
        fieldWithError("Please enter your email address.").assertExists()
        fieldWithError("Please enter your password.").assertExists()
        compose.onAllNodes(hasSetTextAction())[0].performTextInput("capy@")
        fieldWithError("Please enter a valid email address (e.g. name@domain.com).").assertExists()
        compose.onAllNodes(hasSetTextAction())[0].performTextInput("subybara.com")
        compose.onAllNodes(hasSetTextAction())[1].performTextInput("secure123")
        compose.onAllNodes(hasSetTextAction()).assertAll(!SemanticsMatcher.keyIsDefined(SemanticsProperties.Error))
        Espresso.closeSoftKeyboard()
        compose.onNodeWithText("Log In").performScrollTo().performClick()
        compose.runOnIdle { assertTrue(signedIn) }
    }

    @Test fun signInPasswordIsHiddenByDefaultAndCanBeShown() {
        compose.setContent {
            SubybaraTheme { SignInScreen(onSignInSuccess = {}, onForgotPasswordClick = {}, onSignUpClick = {}) }
        }
        val password = compose.onAllNodes(hasSetTextAction())[1]
        password.performTextInput("Test1234!")
        password.assert(hasText("•••••••••"))
        compose.onNodeWithContentDescription("Show password").performClick()
        password.assert(hasText("Test1234!"))
        compose.onNodeWithContentDescription("Hide password").performClick()
        password.assert(hasText("•••••••••"))
    }

    @Test fun wrongDemoPasswordIsShownUnderPasswordAndClearsOnEdit() {
        compose.setContent {
            SubybaraTheme { SignInScreen(onSignInSuccess = {}, onForgotPasswordClick = {}, onSignUpClick = {}) }
        }
        compose.onAllNodes(hasSetTextAction())[0].performTextInput(AccountStore.DemoEmail)
        compose.onAllNodes(hasSetTextAction())[1].performTextInput("wrong")
        Espresso.closeSoftKeyboard()
        compose.onNodeWithText("Log In").performScrollTo().performClick()
        fieldWithError("Incorrect demo password. Please try again.").assertExists()
        compose.onAllNodes(hasSetTextAction())[1].performTextInput("!")
        compose.onNodeWithText("Incorrect demo password. Please try again.").assertDoesNotExist()
    }

    @Test fun signUpShowsEachRuleUnderItsFieldAndClearsWhenCorrected() {
        compose.setContent { SubybaraTheme { SignUpScreen(onSignUpSuccess = {}, onSignInClick = {}) } }
        compose.onNodeWithText("Sign Up").performScrollTo().performClick()
        fieldWithError("Please enter your first name.").assertExists()
        fieldWithError("Please enter your last name.").assertExists()
        fieldWithError("Please enter your email address.").assertExists()
        fieldWithError("Please enter a password.").assertExists()
        fieldWithError("Please re-enter your password.").assertExists()

        compose.onNodeWithText("Juan").performTextInput("123")
        fieldWithError("Names must contain valid letters.").assertExists()
        compose.onNodeWithText("Min. 6 characters").performTextInput("abc")
        fieldWithError("Password must be at least 6 characters.").assertExists()
        compose.onNodeWithText("Re-enter password").performTextInput("abcdef")
        fieldWithError("Passwords do not match.").assertExists()

        compose.onAllNodes(hasSetTextAction())[0].performTextReplacement("Ana")
        compose.onAllNodes(hasSetTextAction())[1].performTextInput("Cruz")
        compose.onAllNodes(hasSetTextAction())[2].performTextInput("ana@example.com")
        compose.onAllNodes(hasSetTextAction())[3].performTextInput("def")
        compose.onAllNodes(hasSetTextAction()).assertAll(!SemanticsMatcher.keyIsDefined(SemanticsProperties.Error))
    }

    @Test fun signUpShowsTakenEmailUnderEmailField() {
        AccountStore.register("Ana Cruz", "ana@example.com")
        compose.setContent { SubybaraTheme { SignUpScreen(onSignUpSuccess = {}, onSignInClick = {}) } }
        compose.onNodeWithText("Juan").performTextInput("Ben")
        compose.onNodeWithText("Dela Cruz").performTextInput("Cruz")
        compose.onNodeWithText("you@example.com").performTextInput("ana@example.com")
        compose.onNodeWithText("Min. 6 characters").performTextInput("secure123")
        compose.onNodeWithText("Re-enter password").performTextInput("secure123")
        Espresso.closeSoftKeyboard()
        compose.onNodeWithText("Sign Up").performScrollTo().performClick()
        fieldWithError("This email already has an account. Please sign in.").assertExists()
        compose.onNodeWithText("Account Created!").assertDoesNotExist()
    }
}
