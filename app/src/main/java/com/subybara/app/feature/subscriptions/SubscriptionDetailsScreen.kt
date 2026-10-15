package com.subybara.app.feature.subscriptions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subybara.app.R
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.core.designsystem.component.SubybaraCard
import com.subybara.app.core.designsystem.theme.Neutral500
import com.subybara.app.core.designsystem.theme.Neutral800
import com.subybara.app.core.designsystem.theme.Primary600
import com.subybara.app.core.designsystem.theme.Primary900
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.SubscriptionStatus
import com.subybara.app.core.model.daysUntil
import com.subybara.app.core.model.parseDate
import com.subybara.app.core.model.reformatDate

private val SubscriptionDetailsDivider = Color(0xFFF1EDDC)

private val CancelSubscriptionBorder = Color(0xFFEEC5BF)

/**
 * Shows subscription details.
 *
 * Calls [onCancelSubscriptionClick] after the user confirms removal.
 *
 * @param subscription Subscription to show.
 * @param onBackClick Called when the back arrow is tapped.
 * @param onEditClick Called when the edit pencil is tapped.
 * @param onCancelSubscriptionClick Called after the user confirms removing the subscription.
 * @param modifier Modifier applied to the screen.
 */
@Composable
fun SubscriptionDetailsScreen(
    subscription: Subscription,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onCancelSubscriptionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    val daysRemaining = parseDate(subscription.renewalDate)?.let { renewalDate ->
        daysUntil(renewalDate)
    }

    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp, end = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_long_left),
                        contentDescription = "Back",
                        tint = Color.Unspecified
                    )
                }

                IconButton(onClick = onEditClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit_subscription),
                        contentDescription = "Edit subscription",
                        tint = Color.Unspecified
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SubscriptionLogo(
                    name = subscription.name,
                    logoUri = subscription.logoUri,
                    size = 56.dp,
                    cornerRadius = 6.dp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = subscription.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        lineHeight = 24.sp
                    ),
                    color = Neutral800,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = subscription.category.label,
                    style = MaterialTheme.typography.bodySmall.copy(
                        lineHeight = 18.sp
                    ),
                    color = Neutral500
                )

                Spacer(modifier = Modifier.height(3.dp))

                SubscriptionStatusLabel(status = subscription.status)

                Spacer(modifier = Modifier.height(19.dp))

                BillingSummaryCard(
                    subscription = subscription,
                    daysRemaining = daysRemaining
                )

                Spacer(modifier = Modifier.height(16.dp))

                SubscriptionDetailsCard(
                    details = listOf(
                        "Billing Cycle" to subscription.billingCycle,
                        "Category" to subscription.category.label,
                        "Renewal Date" to reformatDate(
                            subscription.renewalDate,
                            "d MMM yyyy"
                        ).uppercase(),
                        "Days Remaining" to daysLabel(daysRemaining),
                        "Payment Method" to subscription.paymentMethod.ifBlank { "—" },
                        "Start Date" to reformatDate(
                            subscription.startDate,
                            "MMM d, yyyy"
                        ).ifBlank { "—" },
                        "Notes" to subscription.notes.ifBlank { "—" }
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(12.dp),
                    border = BorderStroke(0.76.dp, CancelSubscriptionBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SubscriptionRed
                    )
                ) {
                    Text(
                        text = "Cancel Subscription",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        DeleteSubscriptionDialog(
            subscriptionName = subscription.name,
            onDeleteClick = {
                // Prevent duplicate removal on repeated taps.
                if (showDeleteDialog) {
                    showDeleteDialog = false
                    onCancelSubscriptionClick()
                }
            },
            onKeepClick = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun SubscriptionStatusLabel(status: SubscriptionStatus) {
    val isActive = status == SubscriptionStatus.ACTIVE
    val label = when (status) {
        SubscriptionStatus.ACTIVE -> "Active"
        SubscriptionStatus.PAUSED -> "Paused"
        SubscriptionStatus.CANCELED -> "Canceled"
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        if (isActive) {
            Icon(
                painter = painterResource(R.drawable.ic_check_circle),
                contentDescription = null,
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.width(4.dp))
        }

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp
            ),
            color = if (isActive) Primary600 else Neutral500
        )
    }
}

@Composable
private fun BillingSummaryCard(
    subscription: Subscription,
    daysRemaining: Int?
) {
    val remaining = if (daysRemaining != null && daysRemaining > 0) {
        "in ${daysLabel(daysRemaining)}"
    } else {
        daysLabel(daysRemaining)
    }

    SubybaraCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "₱%,.2f".format(subscription.amount),
                    style = MaterialTheme.typography.titleLarge,
                    color = Primary900
                )

                Text(
                    text = subscription.billingCycle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral500
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Next billing on",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        lineHeight = 12.sp
                    ),
                    color = Neutral500
                )

                Text(
                    text = reformatDate(subscription.renewalDate, "MMMM d, yyyy"),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        lineHeight = 21.sp
                    ),
                    color = Neutral800
                )

                Text(
                    text = remaining,
                    style = MaterialTheme.typography.bodySmall.copy(
                        lineHeight = 18.sp
                    ),
                    color = Neutral500
                )
            }
        }
    }
}

@Composable
private fun SubscriptionDetailsCard(details: List<Pair<String, String>>) {
    SubybaraCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
            details.forEachIndexed { index, (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        color = Neutral500,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                    Text(
                        text = value,
                        modifier = Modifier.weight(1f),
                        color = Neutral800,
                        textAlign = TextAlign.Right,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                if (index < details.lastIndex) {
                    HorizontalDivider(
                        thickness = 0.76.dp,
                        color = SubscriptionDetailsDivider
                    )
                }
            }
        }
    }
}

private fun daysLabel(days: Int?): String = when {
    days == null -> "—"
    days < 0 -> "Overdue"
    days == 0 -> "Today"
    days == 1 -> "1 day"
    else -> "$days days"
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun SubscriptionDetailsScreenPreview() {
    SubybaraTheme {
        SubscriptionDetailsScreen(
            subscription = SubscriptionStore.subscriptions.first { subscription ->
                subscription.name == "Netflix"
            },
            onBackClick = {},
            onEditClick = {},
            onCancelSubscriptionClick = {}
        )
    }
}
