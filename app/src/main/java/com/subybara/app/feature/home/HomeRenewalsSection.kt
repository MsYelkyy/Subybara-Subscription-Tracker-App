package com.subybara.app.feature.home

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.subybara.app.R
import com.subybara.app.core.designsystem.theme.Neutral500
import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.UpcomingRenewal
import com.subybara.app.core.model.formatDate
import com.subybara.app.core.model.formatSubscriptionAmount
import com.subybara.app.core.model.renewsInLabel
import com.subybara.app.feature.subscriptions.SubscriptionLogo

@Composable
internal fun UpcomingRenewalsSection(
    renewals: List<UpcomingRenewal>,
    onViewAllClick: () -> Unit,
    onSelectSubscription: (Subscription) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Upcoming Renewals",
                modifier = Modifier
                    .weight(1f)
                    .semantics { heading() },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "View all",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onViewAllClick)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = HomeCardGreenBorder, shape = HomeCardShape)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (renewals.isEmpty()) {
                    Text(
                        text = "No upcoming renewals.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Neutral500
                    )
                }
                renewals.forEachIndexed { index, renewal ->
                    DashboardRenewalRow(
                        renewal = renewal,
                        onClick = { onSelectSubscription(renewal.subscription) }
                    )
                    if (index < renewals.lastIndex) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardRenewalRow(
    renewal: UpcomingRenewal,
    onClick: () -> Unit
) {
    val subscription = renewal.subscription
    val countdown = renewsInLabel(renewal.daysLeft).replaceFirstChar { it.uppercase() }
    val renewalText = "$countdown • ${formatDate(renewal.dateMillis, "MMM d")}"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SubscriptionLogo(
            name = subscription.name,
            logoUri = subscription.logoUri
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = subscription.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = renewalText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = formatSubscriptionAmount(subscription.amount),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = "View Details",
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
