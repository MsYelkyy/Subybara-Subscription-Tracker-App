package com.subybara.app.feature.account

import androidx.compose.foundation.background
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subybara.app.R
import com.subybara.app.core.designsystem.component.SubybaraPrimaryButton
import com.subybara.app.core.designsystem.theme.Neutral500
import com.subybara.app.core.designsystem.theme.Neutral800
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.util.AccountValidator

/**
 * Forgot Password screen allowing users to request password recovery instructions.
 *
 * @param onBackToSignIn Called when the user returns to the sign-in screen.
 * @param modifier Modifier applied to the root layout.
 */
@Composable
fun ForgotPasswordScreen(
    onBackToSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var email by rememberSaveable { mutableStateOf("") }
    var isSubmitted by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current

    BackHandler(onBack = onBackToSignIn)

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (isSubmitted) {
            ResetConfirmation(
                email = email.trim(),
                onBackToSignIn = onBackToSignIn,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 24.dp)
            ) {
                IconButton(
                    onClick = onBackToSignIn,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_long_left),
                        contentDescription = "Back to login",
                        tint = Neutral800,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFEDAA),
                            shadowElevation = 3.dp
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_password_cursor),
                                contentDescription = null,
                                tint = Color(0xFFFD8E00),
                                modifier = Modifier.padding(7.dp).size(32.dp)
                            )
                        }
                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = "Forgotten your password?",
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { heading() },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Neutral800,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "There is nothing to worry about, we'll send you a message to help you reset your password.",
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                            color = Neutral500,
                            textAlign = TextAlign.Start
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        BasicTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                errorMessage = null
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = Neutral800
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(1.dp, Color(0xFFC4D9AA), RoundedCornerShape(12.dp)),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (email.isEmpty()) {
                                        Text(
                                            text = "Email address",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF4D5358)
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .semantics { liveRegion = LiveRegionMode.Polite }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        SubybaraPrimaryButton(
                            text = "Send Reset Link",
                            modifier = Modifier.heightIn(min = 44.dp),
                            shape = RoundedCornerShape(24.dp),
                            onClick = {
                                if (!AccountValidator.isValidEmail(email)) {
                                    errorMessage = "Please enter a valid email address."
                                } else {
                                    keyboardController?.hide()
                                    isSubmitted = true
                                }
                            }
                        )

                    }
                }
            }
        }
    }
}

/**
 * Presentation-only confirmation: submitting the form does not send an email.
 */
@Composable
private fun ResetConfirmation(
    email: String,
    onBackToSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 40.dp, bottom = 24.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFEDAA),
                    shadowElevation = 3.dp
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_reset_mail),
                        contentDescription = null,
                        tint = Color(0xFFFD8E00),
                        modifier = Modifier.padding(8.dp).size(34.dp)
                    )
                }
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Check your email",
                    modifier = Modifier.semantics { heading() },
                    style = MaterialTheme.typography.titleLarge,
                    color = Neutral800
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "We have sent the password reset link to your email",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    ),
                    color = Neutral500
                )
                Text(
                    text = "[$email]",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    ),
                    fontWeight = FontWeight.Bold,
                    color = Neutral500
                )
                Spacer(Modifier.height(24.dp))
                SubybaraPrimaryButton(
                    text = "Return to login",
                    onClick = onBackToSignIn,
                    modifier = Modifier.heightIn(min = 44.dp),
                    shape = RoundedCornerShape(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun ResetConfirmationPreview() {
    SubybaraTheme {
        ResetConfirmation(
            email = "customeremail@mail.com",
            onBackToSignIn = {},
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ForgotPasswordScreenPreview() {
    SubybaraTheme {
        ForgotPasswordScreen(
            onBackToSignIn = {}
        )
    }
}
