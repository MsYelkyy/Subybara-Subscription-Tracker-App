package com.subybara.app.feature.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.subybara.app.core.data.AccountStore
import com.subybara.app.core.designsystem.component.SubybaraPrimaryButton
import com.subybara.app.core.designsystem.theme.CardBorder
import com.subybara.app.core.designsystem.theme.GeologicaFontFamily
import com.subybara.app.core.designsystem.theme.Neutral500
import com.subybara.app.core.designsystem.theme.Neutral800
import com.subybara.app.core.designsystem.theme.OffWhite
import com.subybara.app.core.designsystem.theme.Primary600
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.util.AccountValidator

private val WarmBrown = Color(0xFF6A4C32)
private val WarmBrownSubtle = Color(0x8C6A4C32)
private val SignUpButtonColor = Color(0xFFB5CA99)

/**
 * Sign Up screen allowing new users to register an account matching Figma specifications.
 *
 * @param onSignUpSuccess Called when registration is successful and opens the Dashboard.
 * @param onSignInClick Called when the user taps to return to Sign In.
 * @param modifier Modifier applied to the root layout.
 */
@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onSignInClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    var showErrors by rememberSaveable { mutableStateOf(false) }
    var emailTakenError by rememberSaveable { mutableStateOf<String?>(null) }
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }

    val trimmedFirstName = firstName.trim()
    val trimmedLastName = lastName.trim()
    val trimmedEmail = email.trim()

    val firstNameIssue = nameIssue(trimmedFirstName, "Please enter your first name.")
    val lastNameIssue = nameIssue(trimmedLastName, "Please enter your last name.")
    val emailIssue = when {
        trimmedEmail.isBlank() -> "Please enter your email address."
        !AccountValidator.isValidEmail(trimmedEmail) -> "Please enter a valid email address."
        else -> null
    }
    val passwordIssue = when {
        password.isBlank() -> "Please enter a password."
        password.length < 6 -> "Password must be at least 6 characters."
        else -> null
    }
    val confirmPasswordIssue = when {
        confirmPassword.isBlank() -> "Please re-enter your password."
        password != confirmPassword -> "Passwords do not match."
        else -> null
    }

    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = OffWhite)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 80.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Heading
            Column(
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = "Create account",
                    modifier = Modifier.semantics { heading() },
                    style = TextStyle(
                        fontFamily = GeologicaFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 26.sp,
                        lineHeight = 32.sp
                    ),
                    color = WarmBrown
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Start tracking your subscriptions.",
                    style = TextStyle(
                        fontFamily = GeologicaFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    ),
                    color = WarmBrownSubtle
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AccountTextField(
                    label = "First Name",
                    value = firstName,
                    onValueChange = { firstName = it },
                    placeholder = "Juan",
                    error = firstNameIssue.takeIf { showErrors },
                    modifier = Modifier.weight(1f)
                )

                AccountTextField(
                    label = "Last Name",
                    value = lastName,
                    onValueChange = { lastName = it },
                    placeholder = "Dela Cruz",
                    error = lastNameIssue.takeIf { showErrors },
                    modifier = Modifier.weight(1f)
                )
            }

            AccountTextField(
                label = "Email",
                value = email,
                onValueChange = {
                    email = it
                    emailTakenError = null
                },
                placeholder = "you@example.com",
                error = (emailIssue ?: emailTakenError).takeIf { showErrors },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            AccountTextField(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                placeholder = "Min. 6 characters",
                error = passwordIssue.takeIf { showErrors },
                isPassword = true
            )

            AccountTextField(
                label = "Confirm Password",
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "Re-enter password",
                error = confirmPasswordIssue.takeIf { showErrors },
                isPassword = true
            )

            // Terms & Privacy Policy Notice
            Text(
                text = buildAnnotatedString {
                    append("By signing up, you agree to the ")
                    withStyle(
                        style = SpanStyle(
                            fontFamily = GeologicaFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = Primary600
                        )
                    ) {
                        append("Terms & Privacy Policy.")
                    }
                },
                style = TextStyle(
                    fontFamily = GeologicaFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center
                ),
                color = Color(0x996A4C32),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            // Sign Up Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(color = SignUpButtonColor, shape = RoundedCornerShape(20.dp))
                    .clickable {
                        showErrors = true
                        emailTakenError = null
                        val issues = listOf(
                            firstNameIssue,
                            lastNameIssue,
                            emailIssue,
                            passwordIssue,
                            confirmPasswordIssue
                        )

                        if (issues.all { it == null }) {
                            val fullName = "$trimmedFirstName $trimmedLastName".trim()
                            if (AccountStore.register(fullName, trimmedEmail)) {
                                showSuccessDialog = true
                            } else {
                                emailTakenError = "This email already has an account. Please sign in."
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sign Up",
                    style = TextStyle(
                        fontFamily = GeologicaFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    ),
                    color = Color.White
                )
            }

            // Already have an account? Log in Link
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Already have an account? ")
                        withStyle(
                            style = SpanStyle(
                                fontFamily = GeologicaFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                color = Primary600
                            )
                        ) {
                            append("Log in")
                        }
                    },
                    style = TextStyle(
                        fontFamily = GeologicaFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        textAlign = TextAlign.Center
                    ),
                    color = Color(0xB36A4C32),
                    modifier = Modifier.clickable(onClick = onSignInClick)
                )
            }
        }
    }

    // Confirmation Dialog
    if (showSuccessDialog) {
        Dialog(
            onDismissRequest = {
                showSuccessDialog = false
                onSignUpSuccess()
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = OffWhite,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = CardBorder,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Account Created!",
                        style = TextStyle(
                            fontFamily = GeologicaFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        ),
                        color = Neutral800,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Your account has been successfully created. Start tracking your subscriptions.",
                        style = TextStyle(
                            fontFamily = GeologicaFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp
                        ),
                        color = Neutral500,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    SubybaraPrimaryButton(
                        text = "Go to Dashboard",
                        onClick = {
                            showSuccessDialog = false
                            onSignUpSuccess()
                        }
                    )
                }
            }
        }
    }
}

private fun nameIssue(name: String, emptyMessage: String): String? = when {
    name.isBlank() -> emptyMessage
    !AccountValidator.isValidName(name) -> "Names must contain valid letters."
    else -> null
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SignUpScreenPreview() {
    SubybaraTheme {
        SignUpScreen(
            onSignUpSuccess = {},
            onSignInClick = {}
        )
    }
}
