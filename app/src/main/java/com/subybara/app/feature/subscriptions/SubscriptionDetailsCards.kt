package com.subybara.app.feature.subscriptions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subybara.app.R
import com.subybara.app.core.designsystem.component.SubybaraCard
import com.subybara.app.core.designsystem.theme.Neutral500
import com.subybara.app.core.designsystem.theme.Neutral800
import com.subybara.app.core.designsystem.theme.Primary600
import com.subybara.app.core.designsystem.theme.Primary900
import com.subybara.app.core.designsystem.theme.WarmBeige
import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.SubscriptionStatus
import com.subybara.app.core.model.formatDate
import com.subybara.app.core.model.formatSubscriptionAmount

@Composable
internal fun SubscriptionStatusLabel(status: SubscriptionStatus) {
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
internal fun BillingSummaryCard(
    subscription: Subscription,
    billingDate: Long?,
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
                    color = Primary900,
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = 8.sp,
                        maxFontSize = MaterialTheme.typography.titleLarge.fontSize
                    )
                )

                Text(
                    text = subscription.billingCycle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral500,
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = 8.sp,
                        maxFontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
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
                    text = billingDate?.let { date -> formatDate(date, "MMMM d, yyyy") }
                        ?: subscription.renewalDate,
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
internal fun SubscriptionDetailsCard(details: List<Pair<String, String>>) {
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
                        color = WarmBeige
                    )
                }
            }
        }
    }
}

internal fun daysLabel(days: Int?): String = when {
    days == null -> "—"
    days < 0 -> "Overdue"
    days == 0 -> "Today"
    days == 1 -> "1 day"
    else -> "$days days"
}
