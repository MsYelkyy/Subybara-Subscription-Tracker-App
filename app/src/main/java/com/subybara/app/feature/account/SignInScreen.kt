package com.subybara.app.feature.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subybara.app.core.data.AccountStore
import com.subybara.app.core.designsystem.component.SubybaraPrimaryButton
import com.subybara.app.core.designsystem.component.SubybaraTextAction
import com.subybara.app.core.designsystem.theme.Primary600
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.util.AccountValidator

@Composable
fun SignInScreen(
    onSignInSuccess: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showErrors by rememberSaveable { mutableStateOf(false) }
    var signInError by rememberSaveable { mutableStateOf<String?>(null) }

    val trimmedEmail = email.trim()
    val emailIssue = when {
        trimmedEmail.isEmpty() -> "Please enter your email address."
        !AccountValidator.isValidEmail(trimmedEmail) -> "Please enter a valid email address (e.g. name@domain.com)."
        else -> null
    }
    val passwordIssue = "Please enter your password.".takeIf { password.isEmpty() }

    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = maxHeight)
                    .padding(horizontal = 24.dp)
                    .padding(top = 76.dp, bottom = 36.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Welcome back!",
                    modifier = Modifier.semantics { heading() },
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 26.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF705135)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Log in to track your subscriptions.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFA6937C)
                )

                Spacer(modifier = Modifier.height(36.dp))

                AccountTextField(
                    label = "Email",
                    value = email,
                    onValueChange = {
                        email = it
                        signInError = null
                    },
                    placeholder = "you@example.com",
                    error = emailIssue.takeIf { showErrors },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(20.dp))

                AccountTextField(
                    label = "Password",
                    value = password,
                    onValueChange = {
                        password = it
                        signInError = null
                    },
                    placeholder = "••••••••",
                    error = (passwordIssue ?: signInError).takeIf { showErrors },
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(12.dp))
                SubybaraTextAction(
                    text = "Forgot password?",
                    onClick = onForgotPasswordClick,
                    modifier = Modifier.align(Alignment.End),
                    color = Primary600
                )
                Spacer(modifier = Modifier.height(32.dp))
                Spacer(modifier = Modifier.weight(1f))

                SubybaraPrimaryButton(
                    text = "Log In",
                    modifier = Modifier.heightIn(min = 44.dp),
                    shape = RoundedCornerShape(20.dp),
                    onClick = {
                        showErrors = true
                        signInError = null

                        if (emailIssue == null && passwordIssue == null) {
                            if (AccountStore.signIn(trimmedEmail, password)) {
                                onSignInSuccess()
                            } else {
                                signInError = "Incorrect demo password. Please try again."
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SubybaraTextAction(
                    text = "Don't have an account? Sign up",
                    onClick = onSignUpClick,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Primary600
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SignInScreenPreview() {
    SubybaraTheme {
        SignInScreen(
            onSignInSuccess = {},
            onForgotPasswordClick = {},
            onSignUpClick = {}
        )
    }
}
