package com.subybara.app.feature.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subybara.app.R
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.core.designsystem.component.SubybaraTopBar
import com.subybara.app.core.designsystem.theme.Neutral500
import com.subybara.app.core.designsystem.theme.Neutral800
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.model.RenewalReminderDays
import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.UpcomingRenewal
import com.subybara.app.core.model.formatDate
import com.subybara.app.core.model.formatSubscriptionAmount
import com.subybara.app.core.model.renewsInLabel
import com.subybara.app.core.model.upcomingRenewals
import com.subybara.app.feature.subscriptions.SubscriptionLogo

/**
 * Notifications screen listing renewal reminders for active subscriptions that
 * renew within the next [RenewalReminderDays] days.
 *
 * Reminders are created from the subscription data inside the app; nothing is
 * sent by the system or a server.
 *
 * @param reminders Upcoming renewals to show, soonest first.
 * @param onReminderClick Called with the subscription when a reminder is tapped.
 * @param onBackClick Called when the back arrow is tapped.
 * @param modifier Modifier applied to the screen.
 */
@Composable
fun NotificationsScreen(
    reminders: List<UpcomingRenewal>,
    onReminderClick: (Subscription) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SubybaraTopBar(
                title = "Notifications",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        if (reminders.isEmpty()) {
            EmptyNotifications(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Renewing in the next $RenewalReminderDays days",
                        modifier = Modifier.semantics { heading() },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Neutral800
                    )
                }

                items(
                    items = reminders,
                    key = { reminder -> reminder.reminderKey }
                ) { reminder ->
                    ReminderRow(
                        reminder = reminder,
                        onClick = { onReminderClick(reminder.subscription) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReminderRow(
    reminder: UpcomingRenewal,
    onClick: () -> Unit
) {
    val subscription = reminder.subscription

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClickLabel = "View subscription details", onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SubscriptionLogo(
            name = subscription.name,
            logoUri = subscription.logoUri
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${subscription.name} renews ${renewsInLabel(reminder.daysLeft).lowercase()}",
                style = MaterialTheme.typography.labelLarge,
                color = Neutral800
            )
            Text(
                text = "${formatSubscriptionAmount(subscription.amount)} • " +
                    formatDate(reminder.dateMillis, "EEE, MMM d"),
                style = MaterialTheme.typography.bodySmall,
                color = Neutral500
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.Unspecified
        )
    }
}

@Composable
private fun EmptyNotifications(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_bell),
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No notifications yet",
            style = MaterialTheme.typography.titleLarge,
            color = Neutral800,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Renewals due in the next $RenewalReminderDays days will appear here.",
            style = MaterialTheme.typography.bodyMedium,
            color = Neutral500,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NotificationsScreenPreview() {
    SubybaraTheme {
        NotificationsScreen(
            reminders = SubscriptionStore.subscriptions.upcomingRenewals(),
            onReminderClick = {},
            onBackClick = {}
        )
    }
}
