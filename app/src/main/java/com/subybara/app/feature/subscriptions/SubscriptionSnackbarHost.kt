package com.subybara.app.feature.subscriptions

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.subybara.app.R
import com.subybara.app.core.designsystem.theme.Primary600
import com.subybara.app.core.designsystem.theme.White

/**
 * Displays success messages above the screen content.
 * Place it below the status bar, as in SubybaraNavHost.
 *
 * When the message has an action label, such as "Undo", it is shown as a button.
 */
@Composable
fun SubscriptionSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
    ) { snackbarData ->
        val actionLabel = snackbarData.visuals.actionLabel

        Surface(
            modifier = Modifier.padding(12.dp),
            shape = RoundedCornerShape(percent = 50),
            color = Primary600,
            contentColor = White
        ) {
            Row(
                modifier = Modifier.padding(
                    start = 24.dp,
                    top = 6.dp,
                    end = if (actionLabel == null) 36.dp else 12.dp,
                    bottom = 6.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.capy_pencil),
                    contentDescription = null,
                    modifier = Modifier.size(34.dp)
                )

                Text(
                    text = snackbarData.visuals.message,
                    style = MaterialTheme.typography.labelLarge
                )

                if (actionLabel != null) {
                    TextButton(
                        onClick = { snackbarData.performAction() },
                        colors = ButtonDefaults.textButtonColors(contentColor = White)
                    ) {
                        Text(
                            text = actionLabel,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
