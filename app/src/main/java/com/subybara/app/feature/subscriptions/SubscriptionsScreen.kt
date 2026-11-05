package com.subybara.app.feature.subscriptions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subybara.app.R
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.core.designsystem.component.SubybaraBottomBar
import com.subybara.app.core.designsystem.component.SubybaraFilterChip
import com.subybara.app.core.designsystem.component.SubybaraPrimaryButton
import com.subybara.app.core.designsystem.component.SubybaraSearchField
import com.subybara.app.core.designsystem.theme.Neutral800
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.designsystem.theme.WarmBrown
import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.SubscriptionStatus
import com.subybara.app.navigation.SubybaraDestination

/**
 * Lists the user's subscriptions with search and status filter.
 *
 * @param subscriptions Subscriptions to show, usually from SubscriptionStore.
 * @param onSubscriptionClick Called when a subscription row is tapped.
 * @param onDestinationClick Called when a bottom bar destination is tapped.
 * @param onAddSubscriptionClick Called when the center add button is tapped.
 * @param modifier Modifier applied to the screen.
 */
@Composable
fun SubscriptionsScreen(
    subscriptions: List<Subscription>,
    onSubscriptionClick: (Subscription) -> Unit,
    onDestinationClick: (SubybaraDestination) -> Unit,
    onAddSubscriptionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable {
        mutableStateOf(SubscriptionFilter.All)
    }

    val visibleSubscriptions = filterSubscriptions(subscriptions, searchQuery, selectedFilter)

    Scaffold(
        modifier = modifier,
        bottomBar = {
            SubybaraBottomBar(
                selectedDestination = SubybaraDestination.Subscriptions,
                onDestinationClick = onDestinationClick,
                onAddSubscriptionClick = onAddSubscriptionClick
            )
        }
    ) { innerPadding ->

        // Show the empty state only when there are no saved subscriptions.
        if (subscriptions.isEmpty()) {
            EmptySubscriptionsState(
                onAddSubscriptionClick = onAddSubscriptionClick,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 24.dp
                    )
            ) {
                Text(
                    text = "My Subscription",
                    Modifier
                        .fillMaxWidth()
                        .semantics { heading() },
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    color = Neutral800
                )

                Spacer(modifier = Modifier.height(20.dp))

                SubybaraSearchField(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search subscriptions"
                )

                SubscriptionFilterChips(
                    selectedFilter = selectedFilter,
                    onFilterSelect = { selectedFilter = it },
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selectedFilter.label} (${visibleSubscriptions.size})",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (visibleSubscriptions.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "No matching subscriptions",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            "No subscriptions match your search or status filter.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )

                        // Offer to clear only when the user typed a search; a status chip
                        // alone is changed by tapping another chip.
                        if (searchQuery.isNotBlank()) {
                            TextButton(onClick = {
                                searchQuery = ""
                                selectedFilter = SubscriptionFilter.All
                            }) { Text("Clear Filters") }
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(
                            items = visibleSubscriptions,
                            key = { subscription -> subscription.id }
                        ) { subscription ->
                            SubscriptionRow(
                                subscription = subscription,
                                onClick = { onSubscriptionClick(subscription) }
                            )
                        }
                    }
                }
            }
        }
    }
}

internal fun filterSubscriptions(
    subscriptions: List<Subscription>,
    query: String,
    filter: SubscriptionFilter
): List<Subscription> = subscriptions.filter { subscription ->
    subscription.name.contains(query.trim(), ignoreCase = true) && when (filter) {
        SubscriptionFilter.All -> true
        SubscriptionFilter.Active -> subscription.status == SubscriptionStatus.ACTIVE
        SubscriptionFilter.Paused -> subscription.status == SubscriptionStatus.PAUSED
        SubscriptionFilter.Canceled -> subscription.status == SubscriptionStatus.CANCELED
    }
}

// Keep the content centered and allow scrolling on short screens.
@Composable
private fun EmptySubscriptionsState(
    onAddSubscriptionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(R.drawable.capy_no_subscription),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "No subscriptions yet",
                style = MaterialTheme.typography.titleLarge,
                color = Neutral800,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Add your first subscription to start tracking renewals.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    lineHeight = 20.sp
                ),
                color = WarmBrown,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            SubybaraPrimaryButton(
                text = "Add Subscription",
                onClick = onAddSubscriptionClick,
                modifier = Modifier.heightIn(min = 48.dp),
                shape = RoundedCornerShape(22.dp)
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

internal enum class SubscriptionFilter(
    val label: String
) {
    All("All"),
    Active("Active"),
    Paused("Paused"),
    Canceled("Canceled")
}

@Composable
private fun SubscriptionFilterChips(
    selectedFilter: SubscriptionFilter,
    onFilterSelect: (SubscriptionFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SubscriptionFilter.entries.forEach { filter ->
            SubybaraFilterChip(
                label = filter.label,
                selected = filter == selectedFilter,
                onClick = { onFilterSelect(filter) }
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun SubscriptionsScreenPreview() {
    SubybaraTheme {
        SubscriptionsScreen(
            subscriptions = SubscriptionStore.subscriptions,
            onSubscriptionClick = {},
            onDestinationClick = {},
            onAddSubscriptionClick = {}
        )
    }
}
