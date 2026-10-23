package com.subybara.app.feature.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subybara.app.R
import com.subybara.app.core.designsystem.theme.GeologicaFontFamily
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.designsystem.theme.WarmBrown

private val WarmBrownPlaceholder = WarmBrown.copy(alpha = 0.5f)
private val InputBorderColor = Color(0xFFC8D8B0)
private val InputBackgroundColor = Color(0xCCFFFFFF)

/**
 * Labeled text field shared by Sign In and Sign Up.
 *
 * @param error Validation message shown below the field with a red border, or null if valid.
 * @param isPassword Hides the text and adds a show/hide toggle. Hidden by default.
 */
@Composable
internal fun AccountTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = if (isPassword) {
        KeyboardOptions(keyboardType = KeyboardType.Password)
    } else {
        KeyboardOptions.Default
    }
) {
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    val shape = RoundedCornerShape(12.dp)

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = GeologicaFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                lineHeight = 20.sp
            ),
            color = WarmBrown,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            visualTransformation = if (isPassword && !isPasswordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = WarmBrown),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(color = InputBackgroundColor, shape = shape)
                .border(
                    width = if (error != null) 1.dp else 0.76.dp,
                    color = if (error != null) MaterialTheme.colorScheme.error else InputBorderColor,
                    shape = shape
                )
                .semantics { if (error != null) error(error) },
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp, end = if (isPassword) 8.dp else 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyMedium,
                                color = WarmBrownPlaceholder
                            )
                        }
                        innerTextField()
                    }

                    if (isPassword) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable(role = Role.Button) {
                                    isPasswordVisible = !isPasswordVisible
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (isPasswordVisible) R.drawable.ic_eye_off else R.drawable.ic_eye
                                ),
                                contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                tint = WarmBrown,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        )

        if (error != null) {
            Text(
                text = error,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AccountTextFieldPreview() {
    SubybaraTheme {
        Column(modifier = Modifier.padding(24.dp)) {
            AccountTextField(
                label = "Email",
                value = "capy@",
                onValueChange = {},
                placeholder = "you@example.com",
                error = "Please enter a valid email address."
            )
            AccountTextField(
                label = "Password",
                value = "secret",
                onValueChange = {},
                placeholder = "••••••••",
                isPassword = true,
                modifier = Modifier.padding(top = 20.dp)
            )
        }
    }
}
