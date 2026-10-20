package com.subybara.app.feature.account

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subybara.app.core.data.AccountStore
import com.subybara.app.core.designsystem.component.SubybaraPrimaryButton
import com.subybara.app.core.designsystem.component.SubybaraTextAction
import com.subybara.app.core.designsystem.theme.Neutral800
import com.subybara.app.core.designsystem.theme.Primary600
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.util.AccountValidator

@Composable
fun SignInScreen(
    onSignInSuccess: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    val context = LocalContext.current

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

                // Email Field
                AccountFieldLabel("Email")
                Spacer(Modifier.height(8.dp))
                AccountTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    placeholder = "you@example.com",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Password Field
                AccountFieldLabel("Password")
                Spacer(Modifier.height(8.dp))
                AccountTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    placeholder = "••••••••",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation()
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
                    modifier = Modifier.height(44.dp),
                    shape = RoundedCornerShape(20.dp),
                    onClick = {
                        val inputEmail = email.trim()

                        when {
                            inputEmail.isEmpty() || password.isEmpty() -> {
                                errorMessage = "Please enter both email and password."
                                Toast.makeText(context, "Please enter both email and password.", Toast.LENGTH_SHORT).show()
                            }
                            !AccountValidator.isValidEmail(inputEmail) -> {
                                errorMessage = "Please enter a valid email address (e.g. name@domain.com)."
                                Toast.makeText(context, "Invalid email format!", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                errorMessage = null
                                if (AccountStore.currentUser.email != inputEmail) {
                                    AccountStore.updateUser(
                                        name = AccountStore.currentUser.name,
                                        email = inputEmail,
                                    )
                                }
                                onSignInSuccess()
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

@Composable
private fun AccountFieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp, lineHeight = 16.sp),
        color = Color(0xFF705135)
    )
}

@Composable
private fun AccountTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = Neutral800
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(
                color = Color(0xFFFCFDF9),
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
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFD0C7BC)
                    )
                }
                innerTextField()
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SignInScreenPreview() {
    SubybaraTheme {
        SignInScreen(
            onSignInSuccess = {},
            onForgotPasswordClick = {},
            onSignUpClick = {},
            onBackClick = {}
        )
    }
}
