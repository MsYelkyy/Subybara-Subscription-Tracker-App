package com.subybara.app.feature.renewals

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subybara.app.R
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.core.designsystem.component.SubybaraSearchField
import com.subybara.app.core.designsystem.theme.Neutral500
import com.subybara.app.core.designsystem.theme.Neutral800
import com.subybara.app.core.designsystem.theme.Primary600
import com.subybara.app.core.designsystem.theme.Primary900
import com.subybara.app.core.designsystem.theme.Secondary200
import com.subybara.app.core.designsystem.theme.Secondary300
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.designsystem.theme.White
import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.daysUntil
import com.subybara.app.core.model.formatSubscriptionAmount
import com.subybara.app.core.model.parseDate
import com.subybara.app.core.model.reformatDate
import com.subybara.app.feature.subscriptions.SubscriptionLogo

/**
 * Screen displaying borderless list of upcoming renewals with search and filter controls.
 *
 * @param subscriptions Active list of subscriptions, defaults to [SubscriptionStore.subscriptions].
 * @param modifier Applied to root layout.
 * @param onNavigateBack Callback triggered when user clicks the back arrow.
 * @param onSelectSubscription Callback triggered when tapping a renewal item.
 */
@Composable
fun UpcomingRenewalsScreen(
    subscriptions: List<Subscription> = SubscriptionStore.subscriptions,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onSelectSubscription: (Subscription) -> Unit = {}
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf(RenewalFilter.Next30Days) }

    val visibleRenewals = subscriptions
        .filter { it.name.contains(searchQuery.trim(), ignoreCase = true) }
        .filter { selectedFilter.includes(it.renewalDate) }
        .sortedBy { parseDate(it.renewalDate) }

    Scaffold(
        modifier = modifier,
        topBar = { RenewalsTopBar(onNavigateBack = onNavigateBack) }
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

            Text(
                text = "This Month",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Neutral800,
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
            )

            if (visibleRenewals.isEmpty()) {
                EmptyRenewalsMessage()
            } else {
                RenewalList(
                    subscriptions = visibleRenewals,
                    onSelectSubscription = onSelectSubscription
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RenewalsTopBar(onNavigateBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Renewals",
                style = MaterialTheme.typography.titleLarge,
                color = Neutral800
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_long_left),
                    contentDescription = "Return to Dashboard",
                    tint = Neutral800
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
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
            FilterChip(
                selected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) },
                label = { Text(text = filter.label) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Secondary200,
                    labelColor = Neutral800,
                    selectedContainerColor = Primary600,
                    selectedLabelColor = White
                ),
                border = BorderStroke(0.3.dp, Secondary300)
            )
        }
    }
}

@Composable
private fun RenewalList(
    subscriptions: List<Subscription>,
    onSelectSubscription: (Subscription) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(
            items = subscriptions,
            key = { it.id }
        ) { subscription ->
            RenewalItemRow(
                subscription = subscription,
                onClick = { onSelectSubscription(subscription) }
            )
        }
    }
}

@Composable
private fun RenewalItemRow(
    subscription: Subscription,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dayText = reformatDate(subscription.renewalDate, "d")
    val monthText = reformatDate(subscription.renewalDate, "MMM").uppercase()
    val daysRemaining = parseDate(subscription.renewalDate)?.let { daysUntil(it) } ?: 0

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
                text = renewsInText(daysRemaining),
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

private fun renewsInText(days: Int): String = when (days) {
    0 -> "Today"
    1 -> "in 1 day"
    else -> "in $days days"
}

private enum class RenewalFilter(val label: String, val maxDays: Int?) {
    Next7Days("Next 7 Days", 7),
    Next30Days("Next 30 Days", 30),
    AllUpcoming("All Upcoming", null);

    fun includes(renewalDate: String): Boolean {
        val days = parseDate(renewalDate)?.let { daysUntil(it) } ?: return false
        return days >= 0 && (maxDays == null || days <= maxDays)
    }
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
