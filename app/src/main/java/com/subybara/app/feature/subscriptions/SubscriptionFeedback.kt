package com.subybara.app.feature.subscriptions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.subybara.app.R
import com.subybara.app.core.designsystem.component.SubybaraPrimaryButton
import com.subybara.app.core.designsystem.theme.CardBorder
import com.subybara.app.core.designsystem.theme.Neutral600
import com.subybara.app.core.designsystem.theme.Neutral700
import com.subybara.app.core.designsystem.theme.Neutral800
import com.subybara.app.core.designsystem.theme.Primary600
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.designsystem.theme.White


// Shared subscription colors from the design.
internal val SubscriptionBrown = Color(0xFF6A4C32)
internal val SubscriptionRed = Color(0xFFAC121B)

private val DialogShape = RoundedCornerShape(28.dp)
private val WideButtonShape = RoundedCornerShape(20.dp)
private val SmallButtonShape = RoundedCornerShape(12.dp)

/**
 * Displays success messages above the screen content.
 * Place it below the status bar, as in SubybaraNavHost.
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
                    end = 36.dp,
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
            }
        }
    }
}

@Composable
fun DiscardChangesDialog(
    onDiscardClick: () -> Unit,
    onKeepEditingClick: () -> Unit
) {
    SubscriptionDialog(onDismissRequest = onKeepEditingClick) {
        DialogFace()
        DialogTitle("Discard Changes?")
        DialogMessage(AnnotatedString("If you go back now, you will lose your changes."))

        Column(
            modifier = Modifier.padding(top = 22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SubybaraPrimaryButton(
                text = "Discard Changes",
                onClick = onDiscardClick,
                modifier = Modifier.heightIn(min = 48.dp),
                shape = WideButtonShape
            )

            DialogSecondaryButton(
                text = "Keep Editing",
                onClick = onKeepEditingClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                shape = WideButtonShape
            )
        }
    }
}

@Composable
fun SaveChangesDialog(
    onYesClick: () -> Unit,
    onNoClick: () -> Unit
) {
    SubscriptionDialog(onDismissRequest = onNoClick) {
        Image(
            painter = painterResource(R.drawable.capy_pencil),
            contentDescription = null,
            modifier = Modifier.height(140.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        DialogTitle("Save changes?")

        Row(
            modifier = Modifier.padding(top = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(50.dp)
        ) {
            SubybaraPrimaryButton(
                text = "Yes",
                onClick = onYesClick,
                modifier = Modifier
                    .width(80.dp)
                    .heightIn(min = 48.dp),
                shape = SmallButtonShape
            )

            DialogSecondaryButton(
                text = "No",
                onClick = onNoClick,
                modifier = Modifier
                    .width(80.dp)
                    .heightIn(min = 48.dp),
                shape = SmallButtonShape
            )
        }
    }
}

/**
 * Shows confirmation after the subscription has been saved.
 */
@Composable
fun SubscriptionAddedDialog(
    subscriptionName: String,
    onDoneClick: () -> Unit
) {
    SubscriptionDialog(
        onDismissRequest = onDoneClick,
        dismissOnClickOutside = false
    ) {
        DialogFace()
        DialogTitle("Added successfully!")
        DialogMessage(
            buildAnnotatedString {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append(subscriptionName)
                pop()
                append(" has been added to your Subybara tracker.")
            }
        )

        SubybaraPrimaryButton(
            text = "Done",
            onClick = onDoneClick,
            modifier = Modifier
                .padding(top = 24.dp)
                .heightIn(min = 48.dp),
            shape = WideButtonShape
        )
    }
}

@Composable
fun DeleteSubscriptionDialog(
    subscriptionName: String,
    onDeleteClick: () -> Unit,
    onKeepClick: () -> Unit
) {
    SubscriptionDialog(onDismissRequest = onKeepClick) {
        Image(
            painter = painterResource(R.drawable.capy_edit_subscription),
            contentDescription = null,
            modifier = Modifier.height(140.dp)
        )

        Text(
            text = "Are you sure you want to stop tracking $subscriptionName? " +
                "It will be removed from your list.",
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 13.sp,
                lineHeight = 16.sp
            ),
            color = SubscriptionBrown,
            textAlign = TextAlign.Center
        )

        Column(
            modifier = Modifier.padding(top = 22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SubybaraPrimaryButton(
                text = "Remove Subscription",
                onClick = onDeleteClick,
                modifier = Modifier.heightIn(min = 48.dp),
                shape = WideButtonShape,
                containerColor = SubscriptionRed
            )

            DialogSecondaryButton(
                text = "Keep Subscription",
                onClick = onKeepClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                shape = WideButtonShape
            )
        }
    }
}

@Composable
private fun SubscriptionDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dismissOnClickOutside: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    if (LocalInspectionMode.current) {
        // Render the card directly in previews.
        DialogCard(
            modifier = modifier,
            content = content
        )
    } else {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                dismissOnClickOutside = dismissOnClickOutside,
                usePlatformDefaultWidth = false
            )
        ) {
            val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window

            // Match the background dimming in the design.
            SideEffect {
                dialogWindow?.setDimAmount(0.4f)
            }

            DialogCard(
                modifier = modifier,
                content = content
            )
        }
    }
}

@Composable
private fun DialogCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .clip(DialogShape)
            .background(MaterialTheme.colorScheme.background)
            .padding(
                horizontal = 27.dp,
                vertical = 28.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Composable
private fun DialogFace() {
    Image(
        painter = painterResource(R.drawable.capy_discard_changes_img),
        contentDescription = null,
        modifier = Modifier
            .padding(bottom = 14.dp)
            .size(56.dp)
    )
}

@Composable
private fun DialogTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = Neutral800,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun DialogMessage(text: AnnotatedString) {
    Text(
        text = text,
        modifier = Modifier.padding(top = 6.dp),
        style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 13.sp,
            lineHeight = 20.sp
        ),
        color = Neutral700,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun DialogSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = WideButtonShape
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.dropShadow(
            shape = shape,
            shadow = Shadow(
                radius = 2.dp,
                spread = 0.dp,
                color = Color.Black.copy(alpha = 0.25f),
                offset = DpOffset(0.dp, 0.5.dp)
            )
        ),
        shape = shape,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = White,
            contentColor = Neutral600
        ),
        border = BorderStroke(1.dp, CardBorder),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(
    showBackground = true,
    heightDp = 1500
)
@Composable
private fun SubscriptionDialogsPreview() {
    SubybaraTheme {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DiscardChangesDialog(
                onDiscardClick = {},
                onKeepEditingClick = {}
            )

            SaveChangesDialog(
                onYesClick = {},
                onNoClick = {}
            )

            SubscriptionAddedDialog(
                subscriptionName = "Spotify",
                onDoneClick = {}
            )

            DeleteSubscriptionDialog(
                subscriptionName = "Netflix",
                onDeleteClick = {},
                onKeepClick = {}
            )
        }
    }
}
