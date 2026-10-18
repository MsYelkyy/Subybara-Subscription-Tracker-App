package com.subybara.app.feature.renewals

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subybara.app.R
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.model.Subscription
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Screen displaying borderless list of upcoming renewals with filter controls.
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
    var selectedFilter by rememberSaveable { mutableStateOf(RenewalFilter.Next30Days) }

    val sortedSubscriptions = subscriptions.sortedBy { it.renewalDate }

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
            RenewalFilterChips(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            Text(
                text = "This Month",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
            )

            if (sortedSubscriptions.isEmpty()) {
                EmptyRenewalsMessage()
            } else {
                RenewalList(
                    subscriptions = sortedSubscriptions,
                    onSelectSubscription = onSelectSubscription
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RenewalsTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Renewals",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_long_left),
                    contentDescription = "Return to Dashboard",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = "Search Renewals",
                    tint = MaterialTheme.colorScheme.onBackground
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
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            RenewalFilter.entries.forEach { filter ->
                val isSelected = filter == selectedFilter
                FilterChip(
                    selected = isSelected,
                    onClick = { onFilterSelected(filter) },
                    label = { Text(text = filter.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
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
    val (dayText, monthText) = parseDateBadges(subscription.renewalDate)
    val daysRemaining = calculateDaysRemaining(subscription.renewalDate)

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
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = monthText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Subscription app Logo
        SubscriptionLogo(name = subscription.name)

        Spacer(modifier = Modifier.width(12.dp))

        // Subscription Name+Category
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = subscription.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subscription.category.label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Price + Days Remaining
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "₱${String.format(Locale.US, "%,.2f", subscription.amount)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "in $daysRemaining days",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = "View Details",
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
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
            painter = painterResource(id = logoRes),
            contentDescription = "$name logo",
            modifier = logoModifier,
            contentScale = ContentScale.Fit
        )
    } else {
        Box(
            modifier = logoModifier.background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
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
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun parseDateBadges(rawDate: String): Pair<String, String> {
    return runCatching {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = sdf.parse(rawDate) ?: Date()
        val cal = Calendar.getInstance().apply { time = date }

        val day = cal.get(Calendar.DAY_OF_MONTH).toString()
        val monthSdf = SimpleDateFormat("MMM", Locale.US)
        val month = monthSdf.format(date).uppercase()
        Pair(day, month)
    }.getOrElse {
        val rawDay = rawDate
            .split("-", "/", " ")
            .lastOrNull { it.all { char -> char.isDigit() } && it.length <= 2 }
            ?.toIntOrNull() ?: 1
        Pair(rawDay.toString(), "JUL")
    }
}

private fun calculateDaysRemaining(rawDate: String): Long {
    return runCatching {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val targetDate = sdf.parse(rawDate) ?: Date()
        val diffMs = targetDate.time - System.currentTimeMillis()
        val days = TimeUnit.MILLISECONDS.toDays(diffMs)
        if (days < 0) 0L else days
    }.getOrDefault(10L)
}

@DrawableRes
private fun logoFor(name: String): Int? = when {
    name.contains("netflix", ignoreCase = true) -> R.drawable.logo_netflix
    name.contains("spotify", ignoreCase = true) -> R.drawable.logo_spotify
    name.contains("youtube", ignoreCase = true) -> R.drawable.logo_youtube
    else -> null
}

private enum class RenewalFilter(val label: String) {
    Next7Days("Next 7 Days"),
    Next30Days("Next 30 Days"),
    AllUpcoming("All Upcoming")
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