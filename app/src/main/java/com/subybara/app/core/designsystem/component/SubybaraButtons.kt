package com.subybara.app.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.subybara.app.core.designsystem.theme.Primary600
import com.subybara.app.core.designsystem.theme.White

/**
 * Main filled action button used for primary actions.
 *
 * [containerColor] is green by default; pass a different color for
 * destructive actions such as deleting.
 */
@Composable
fun SubybaraPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(15.dp),
    containerColor: Color = Primary600
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = White,
            disabledContainerColor = containerColor.copy(alpha = 0.4f),
            disabledContentColor = White.copy(alpha = 0.7f)
        ),
        contentPadding = PaddingValues(
            horizontal = 20.dp,
            vertical = 14.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Lightweight text action used for secondary actions such as
 * Sign In, Sign Up, or Forgot Password.
 */
@Composable
fun SubybaraTextAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Primary600
) {
    Box(
        modifier = modifier
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .padding(
                horizontal = 8.dp,
                vertical = 4.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}
