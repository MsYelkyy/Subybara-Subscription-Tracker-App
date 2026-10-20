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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import com.subybara.app.core.designsystem.theme.Primary800
import com.subybara.app.core.designsystem.theme.Primary900
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.designsystem.theme.White
import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.SubscriptionStatus
import com.subybara.app.core.model.daysUntil
import com.subybara.app.core.model.formatSubscriptionAmount
import com.subybara.app.core.model.parseDate
import com.subybara.app.core.model.reformatDate

private val SubscriptionDetailsDivider = Color(0xFFF1EDDC)

private val RemoveSubscriptionBorder = Color(0xFFEEC5BF)

/**
 * Shows subscription details.
 *
 * Calls [onRemoveSubscriptionClick] after the user confirms removal.
 *
 * @param subscription Subscription to show.
 * @param onBackClick Called when the back arrow is tapped.
 * @param onEditClick Called when the edit pencil is tapped.
 * @param onRemoveSubscriptionClick Called after the user confirms removing the subscription.
 * @param onStatusChange Updates the tracking status without contacting a provider.
 * @param modifier Modifier applied to the screen.
 */
@Composable
fun SubscriptionDetailsScreen(
    subscription: Subscription,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onRemoveSubscriptionClick: () -> Unit,
    onStatusChange: (SubscriptionStatus) -> Unit,
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

                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_info),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Primary600
                    )

                    Text(
                        text = "These actions only update your tracker.\nManage payments with your provider.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Neutral500
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        onStatusChange(
                            if (subscription.status == SubscriptionStatus.ACTIVE)
                                SubscriptionStatus.PAUSED else SubscriptionStatus.ACTIVE
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary600,
                        contentColor = White
                    )
                ) {
                    ActionContent(
                        icon = if (subscription.status == SubscriptionStatus.ACTIVE)
                            R.drawable.ic_pause else R.drawable.ic_play,
                        text = when (subscription.status) {
                            SubscriptionStatus.ACTIVE -> "Pause Subscription"
                            SubscriptionStatus.PAUSED -> "Resume Subscription"
                            SubscriptionStatus.CANCELED -> "Mark as Active"
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (subscription.status != SubscriptionStatus.CANCELED) {
                        OutlinedButton(
                            onClick = { onStatusChange(SubscriptionStatus.CANCELED) },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
                            border = BorderStroke(1.dp, Primary600),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Primary800
                            )
                        ) {
                            ActionContent(icon = R.drawable.ic_block, text = "Mark as Canceled")
                        }
                    }

                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
                        border = BorderStroke(0.76.dp, RemoveSubscriptionBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = SubscriptionRed
                        )
                    ) {
                        ActionContent(icon = R.drawable.ic_delete, text = "Remove Subscription")
                    }
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
                    onRemoveSubscriptionClick()
                }
            },
            onKeepClick = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun ActionContent(icon: Int, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
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
    val icon = when (status) {
        SubscriptionStatus.ACTIVE -> R.drawable.ic_check_circle
        SubscriptionStatus.PAUSED -> R.drawable.ic_pause_circle
        SubscriptionStatus.CANCELED -> R.drawable.ic_cancel_circle
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.width(4.dp))

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
                    text = formatSubscriptionAmount(subscription.amount),
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
                    text = if (subscription.status == SubscriptionStatus.ACTIVE) "Next billing on" else "Recorded billing date",
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
            onRemoveSubscriptionClick = {},
            onStatusChange = {}
        )
    }
}
