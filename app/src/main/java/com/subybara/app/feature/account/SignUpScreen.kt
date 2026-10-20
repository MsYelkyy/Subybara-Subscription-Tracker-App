package com.subybara.app.feature.account

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.subybara.app.R
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
private val WarmBrownPlaceholder = Color(0x806A4C32)
private val InputBorderColor = Color(0xFFC8D8B0)
private val InputBackgroundColor = Color(0xCCFFFFFF)
private val SignUpButtonColor = Color(0xFFB5CA99)

/**
 * Sign Up screen allowing new users to register an account matching Figma specifications.
 *
 * @param onSignUpSuccess Called when registration is successful and navigates back to Sign In.
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

    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isConfirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }

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

            // First Name & Last Name Fields (Side by side)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // First Name
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "First Name",
                        style = TextStyle(
                            fontFamily = GeologicaFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        ),
                        color = WarmBrown,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    FigmaLabeledTextField(
                        value = firstName,
                        onValueChange = {
                            firstName = it
                            errorMessage = null
                        },
                        placeholder = "Juan"
                    )
                }

                // Last Name
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Last Name",
                        style = TextStyle(
                            fontFamily = GeologicaFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        ),
                        color = WarmBrown,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    FigmaLabeledTextField(
                        value = lastName,
                        onValueChange = {
                            lastName = it
                            errorMessage = null
                        },
                        placeholder = "Dela Cruz"
                    )
                }
            }

            // Email Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Email",
                    style = TextStyle(
                        fontFamily = GeologicaFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    ),
                    color = WarmBrown,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FigmaLabeledTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    placeholder = "you@example.com",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }

            // Password Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Password",
                    style = TextStyle(
                        fontFamily = GeologicaFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    ),
                    color = WarmBrown,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FigmaLabeledTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    placeholder = "Min. 6 characters",
                    isPassword = true,
                    isPasswordVisible = isPasswordVisible,
                    onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                    trailingIconRes = if (isPasswordVisible) R.drawable.ic_eye_off else R.drawable.ic_eye
                )
            }

            // Confirm Password Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Confirm Password",
                    style = TextStyle(
                        fontFamily = GeologicaFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    ),
                    color = WarmBrown,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FigmaLabeledTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        errorMessage = null
                    },
                    placeholder = "Re-enter password",
                    isPassword = true,
                    isPasswordVisible = isConfirmPasswordVisible,
                    onTogglePasswordVisibility = { isConfirmPasswordVisible = !isConfirmPasswordVisible },
                    trailingIconRes = if (isConfirmPasswordVisible) R.drawable.ic_eye else R.drawable.ic_eye_off
                )
            }

            // Error Message
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    style = TextStyle(
                        fontFamily = GeologicaFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }

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
                        val trimmedFirstName = firstName.trim()
                        val trimmedLastName = lastName.trim()
                        val fullName = "$trimmedFirstName $trimmedLastName".trim()
                        val trimmedEmail = email.trim()

                        when {
                            trimmedFirstName.isBlank() || trimmedLastName.isBlank() || trimmedEmail.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                                errorMessage = "Please fill in all required fields."
                            }
                            !AccountValidator.isValidName(trimmedFirstName) || !AccountValidator.isValidName(trimmedLastName) -> {
                                errorMessage = "Names must contain valid letters."
                            }
                            !AccountValidator.isValidEmail(trimmedEmail) -> {
                                errorMessage = "Please enter a valid email address."
                            }
                            password.length < 6 -> {
                                errorMessage = "Password must be at least 6 characters."
                            }
                            password != confirmPassword -> {
                                errorMessage = "Passwords do not match."
                            }
                            else -> {
                                errorMessage = null
                                AccountStore.updateUser(fullName, trimmedEmail)
                                showSuccessDialog = true
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
                        text = "Your account has been successfully created. You can now sign in.",
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
                        text = "Proceed to Sign In",
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

@Composable
private fun FigmaLabeledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePasswordVisibility: (() -> Unit)? = null,
    @DrawableRes trailingIconRes: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        textStyle = TextStyle(
            fontFamily = GeologicaFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = WarmBrown
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(
                color = InputBackgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 0.76.dp,
                color = InputBorderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = TextStyle(
                                fontFamily = GeologicaFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp
                            ),
                            color = WarmBrownPlaceholder
                        )
                    }
                    innerTextField()
                }

                if (isPassword && trailingIconRes != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(onClick = { onTogglePasswordVisibility?.invoke() }),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = trailingIconRes),
                            contentDescription = "Toggle password visibility",
                            tint = WarmBrown,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    )
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
