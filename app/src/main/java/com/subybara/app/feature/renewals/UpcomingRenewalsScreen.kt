package com.subybara.app.feature.renewals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subybara.app.R
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.core.designsystem.component.SubybaraFilterChip
import com.subybara.app.core.designsystem.component.SubybaraSearchField
import com.subybara.app.core.designsystem.component.SubybaraTopBar
import com.subybara.app.core.designsystem.theme.Neutral500
import com.subybara.app.core.designsystem.theme.Neutral800
import com.subybara.app.core.designsystem.theme.Primary900
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.UpcomingRenewal
import com.subybara.app.core.model.formatDate
import com.subybara.app.core.model.formatSubscriptionAmount
import com.subybara.app.core.model.renewalMonthLabel
import com.subybara.app.core.model.renewsInLabel
import com.subybara.app.core.model.upcomingRenewals
import com.subybara.app.feature.subscriptions.SubscriptionLogo

/**
 * Screen displaying borderless list of upcoming renewals with search and filter controls.
 *
 * @param subscriptions List of user subscriptions, usually [SubscriptionStore.subscriptions].
 * @param modifier Applied to root layout.
 * @param onNavigateBack Callback triggered when user clicks the back arrow.
 * @param onSelectSubscription Callback triggered when tapping a renewal item.
 */
@Composable
fun UpcomingRenewalsScreen(
    subscriptions: List<Subscription>,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onSelectSubscription: (Subscription) -> Unit = {}
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf(RenewalFilter.Next30Days) }

    val visibleRenewals = subscriptions
        .upcomingRenewals(withinDays = selectedFilter.maxDays)
        .filter { renewal -> renewal.subscription.name.contains(searchQuery.trim(), ignoreCase = true) }

    Scaffold(
        modifier = modifier,
        topBar = {
            SubybaraTopBar(
                title = "Renewals",
                onBackClick = onNavigateBack,
                backContentDescription = "Return to Dashboard"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            SubybaraSearchField(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search Renewals"
            )

            RenewalFilterChips(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            if (visibleRenewals.isEmpty()) {
                EmptyRenewalsMessage()
            } else {
                RenewalList(
                    renewals = visibleRenewals,
                    onSelectSubscription = onSelectSubscription
                )
            }
        }
    }
}

@Composable
private fun RenewalFilterChips(
    selectedFilter: RenewalFilter,
    onFilterSelected: (RenewalFilter) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(vertical = 12.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        RenewalFilter.entries.forEach { filter ->
            SubybaraFilterChip(
                label = filter.label,
                selected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) }
            )
        }
    }
}

@Composable
private fun RenewalList(
    renewals: List<UpcomingRenewal>,
    onSelectSubscription: (Subscription) -> Unit
) {
    val renewalsByMonth = renewals.groupBy { renewal -> renewalMonthLabel(renewal.dateMillis) }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        renewalsByMonth.forEach { (month, monthRenewals) ->
            item(key = month) {
                Text(
                    text = month,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Neutral800,
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 12.dp)
                        .semantics { heading() }
                )
            }

            items(
                items = monthRenewals,
                key = { renewal -> renewal.subscription.id }
            ) { renewal ->
                RenewalItemRow(
                    renewal = renewal,
                    onClick = { onSelectSubscription(renewal.subscription) },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun RenewalItemRow(
    renewal: UpcomingRenewal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subscription = renewal.subscription
    val dayText = formatDate(renewal.dateMillis, "d")
    val monthText = formatDate(renewal.dateMillis, "MMM").uppercase()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // BorderLEss Date Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            Text(
                text = dayText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Neutral800
            )
            Text(
                text = monthText,
                style = MaterialTheme.typography.bodySmall,
                color = Neutral500,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Subscription app Logo
        SubscriptionLogo(
            name = subscription.name,
            logoUri = subscription.logoUri
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Subscription Name+Category
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = subscription.name,
                style = MaterialTheme.typography.labelLarge,
                color = Neutral800,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subscription.category.label,
                style = MaterialTheme.typography.bodySmall,
                color = Neutral500
            )
        }

        // Price + Days Remaining
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = formatSubscriptionAmount(subscription.amount),
                style = MaterialTheme.typography.labelMedium,
                color = Primary900
            )
            Text(
                text = renewsInLabel(renewal.daysLeft),
                style = MaterialTheme.typography.bodySmall,
                color = Neutral500
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = "View Details",
            modifier = Modifier.size(20.dp),
            tint = Color.Unspecified
        )
    }
}

@Composable
private fun EmptyRenewalsMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No upcoming renewals found.",
            style = MaterialTheme.typography.bodyMedium,
            color = Neutral500
        )
    }
}

private enum class RenewalFilter(val label: String, val maxDays: Int?) {
    Next7Days("Next 7 Days", 7),
    Next30Days("Next 30 Days", 30),
    AllUpcoming("All Upcoming", null)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun UpcomingRenewalsScreenPreview() {
    SubybaraTheme {
        UpcomingRenewalsScreen(
            subscriptions = SubscriptionStore.subscriptions
        )
    }
}
