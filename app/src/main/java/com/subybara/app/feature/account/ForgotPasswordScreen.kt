package com.subybara.app.feature.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subybara.app.core.designsystem.component.SubybaraPrimaryButton
import com.subybara.app.core.designsystem.component.SubybaraTextAction
import com.subybara.app.core.designsystem.theme.Neutral500
import com.subybara.app.core.designsystem.theme.Neutral800
import com.subybara.app.core.designsystem.theme.Primary600
import com.subybara.app.core.designsystem.theme.Secondary200
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

    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Reset Password",
                style = MaterialTheme.typography.titleLarge,
                color = Neutral800,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter your email address and we'll send you recovery instructions.",
                style = MaterialTheme.typography.bodyMedium,
                color = Neutral500,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isSubmitted) {
                Text(
                    text = "Recovery instructions sent to $email!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Primary600,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                SubybaraPrimaryButton(
                    text = "Back to Sign In",
                    onClick = onBackToSignIn
                )
            } else {
                BasicTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = Neutral800
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(
                            color = Secondary200,
                            shape = RoundedCornerShape(15.dp)
                        ),
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
                                    color = Neutral500
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
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                SubybaraPrimaryButton(
                    text = "Send Instructions",
                    onClick = {
                        if (!AccountValidator.isValidEmail(email)) {
                            errorMessage = "Please enter a valid email address."
                        } else {
                            isSubmitted = true
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SubybaraTextAction(
                    text = "Back to Sign In",
                    onClick = onBackToSignIn,
                    color = Primary600
                )
            }
        }
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
