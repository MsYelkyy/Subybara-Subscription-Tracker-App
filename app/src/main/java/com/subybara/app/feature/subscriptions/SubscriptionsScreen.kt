package com.subybara.app.feature.subscriptions

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subybara.app.R
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.core.designsystem.component.SubybaraBottomBar
import com.subybara.app.core.designsystem.theme.Neutral500
import com.subybara.app.core.designsystem.theme.Neutral800
import com.subybara.app.core.designsystem.theme.Primary200
import com.subybara.app.core.designsystem.theme.Primary600
import com.subybara.app.core.designsystem.theme.Primary900
import com.subybara.app.core.designsystem.theme.Secondary200
import com.subybara.app.core.designsystem.theme.Secondary300
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.designsystem.theme.White
import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.SubscriptionStatus
import com.subybara.app.navigation.SubybaraDestination

/**
 * Lists the user's subscriptions with search and status filter.
 *
 * @param subscriptions Subscriptions to show, usually from SubscriptionStore.
 * @param onDestinationClick Called when a bottom bar destination is tapped.
 * @param onAddSubscriptionClick Called when the center add button is tapped.
 * @param modifier Modifier applied to the screen.
 */
@Composable
fun SubscriptionsScreen(
    subscriptions: List<Subscription>,
    onDestinationClick: (SubybaraDestination) -> Unit,
    onAddSubscriptionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable {
        mutableStateOf(SubscriptionFilter.All)
    }

    val searchResults = subscriptions.filter { subscription ->
        subscription.name.contains(
            searchQuery.trim(),
            ignoreCase = true
        )
    }

    val visibleSubscriptions = searchResults.filter { subscription ->
        when (selectedFilter) {
            SubscriptionFilter.All -> true

            SubscriptionFilter.Active ->
                subscription.status == SubscriptionStatus.ACTIVE

            SubscriptionFilter.Paused ->
                subscription.status == SubscriptionStatus.PAUSED

            SubscriptionFilter.Canceled ->
                subscription.status == SubscriptionStatus.CANCELED
        }
    }

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
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                color = Neutral800
            )

            Spacer(modifier = Modifier.height(20.dp))

            SubscriptionSearchField(
                searchQuery = searchQuery,
                onQueryChange = { searchQuery = it }
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

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(
                    items = visibleSubscriptions,
                    key = { subscription -> subscription.id }
                ) { subscription ->
                    SubscriptionRow(
                        subscription = subscription
                    )
                }
            }
        }
    }
}

private enum class SubscriptionFilter(
    val label: String
) {
    All("All"),
    Active("Active"),
    Paused("Paused"),
    Canceled("Canceled")
}

@Composable
private fun SubscriptionSearchField(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = searchQuery,
        onValueChange = onQueryChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = Neutral800
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(
                color = Secondary200,
                shape = RoundedCornerShape(20.dp)
            ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = "Search",
                    tint = Color.Unspecified
                )
                Spacer(Modifier.width(8.dp))

                Box{
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search subscriptions",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Neutral500
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}

@Composable
private fun SubscriptionFilterChips(
    selectedFilter: SubscriptionFilter,
    onFilterSelect: (SubscriptionFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SubscriptionFilter.entries.forEach { filter ->

            FilterChip(
                selected = filter == selectedFilter,
                onClick = {
                    onFilterSelect(filter)
                },
                label = {
                    Text(text = filter.label)
                },
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
private fun SubscriptionRow(
    subscription: Subscription,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SubscriptionLogo(
            name = subscription.name
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = subscription.name,
                style = MaterialTheme.typography.labelLarge,
                color = Neutral800,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subscription.category.label,
                style = MaterialTheme.typography.bodySmall


            )

            Text(
                text = "Renews on ${subscription.renewalDate}",
                style = MaterialTheme.typography.bodySmall,
                color = Neutral500
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "₱%,.2f".format(subscription.amount),
                style = MaterialTheme.typography.labelMedium,
                color = Primary900
            )

            Text(
                text = subscription.billingCycle,
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
private fun SubscriptionLogo(
    name: String,
    modifier: Modifier = Modifier
) {
    val logoModifier = modifier
        .size(40.dp)
        .clip(RoundedCornerShape(10.dp))

    val logoRes = logoFor(name)

    if (logoRes != null) {
        Image(
            painter = painterResource(
                id = logoRes
            ),
            contentDescription = "$name logo",
            modifier = logoModifier,
            contentScale = ContentScale.Fit
        )
    } else {
        Box(
            modifier = logoModifier
                .background(Primary200),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = Primary900
            )
        }
    }
}

@DrawableRes
private fun logoFor(
    name: String
): Int? = when {
    name.contains(
        "netflix",
        ignoreCase = true
    ) -> R.drawable.logo_netflix

    name.contains(
        "spotify",
        ignoreCase = true
    ) -> R.drawable.logo_spotify

    name.contains(
        "youtube",
        ignoreCase = true
    ) -> R.drawable.logo_youtube

    else -> null
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
            onDestinationClick = {},
            onAddSubscriptionClick = {}
        )
    }
}
