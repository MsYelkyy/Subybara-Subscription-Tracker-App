package com.subybara.app.feature.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subybara.app.R
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.core.designsystem.component.SubybaraBottomBar
import com.subybara.app.core.designsystem.component.SubybaraCard
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.model.Subscription
import com.subybara.app.navigation.SubybaraDestination
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Dashboard screen displaying spending metrics, upcoming renewal list,
 * and spending insights synced with [SubscriptionStore].
 */
@Composable
fun HomeScreen(
    subscriptions: List<Subscription> = SubscriptionStore.subscriptions,
    onDestinationClick: (SubybaraDestination) -> Unit = {},
    onAddSubscriptionClick: () -> Unit = {},
    onViewAllRenewalsClick: () -> Unit = {},
    onSelectSubscription: (Subscription) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val totalMonthlySpend = subscriptions.sumOf { it.amount }
    val totalAnnualSpend = totalMonthlySpend * 12
    val activeCount = subscriptions.size
    val upcomingRenewals = subscriptions.sortedBy { it.renewalDate }.take(5)

    Scaffold(
        modifier = modifier,
        bottomBar = {
            SubybaraBottomBar(
                selectedDestination = SubybaraDestination.Home,
                onDestinationClick = onDestinationClick,
                onAddSubscriptionClick = onAddSubscriptionClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { HomeHeader() }

            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(top = 100.dp)) {
                        OverviewMetricsCard(
                            monthlySpend = totalMonthlySpend,
                            annualSpend = totalAnnualSpend,
                            activeCount = activeCount
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.capy_home_heading),
                        contentDescription = "Capybara header illustration",
                        modifier = Modifier
                            .height(115.dp)
                            .align(Alignment.TopEnd)
                            .padding(end = 8.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            item { SpendingTrendSection() }

            item {
                UpcomingRenewalsSection(
                    renewals = upcomingRenewals,
                    onViewAllClick = onViewAllRenewalsClick,
                    onSelectSubscription = onSelectSubscription
                )
            }

            item {
                SpendingInsightsSection(
                    totalMonthlySpend = totalMonthlySpend,
                    subscriptions = subscriptions,
                    onViewInsightsClick = { onDestinationClick(SubybaraDestination.Insights) }
                )
            }
        }
    }
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Good Morning!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Here's your subscription overview",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "Notifications",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun OverviewMetricsCard(
    monthlySpend: Double,
    annualSpend: Double,
    activeCount: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Total Monthly Spend",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "₱${String.format(Locale.US, "%,.2f", monthlySpend)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "First month tracked",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Annual Spend",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "₱${String.format(Locale.US, "%,.2f", annualSpend)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Active Subscription",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                    Text(
                        text = activeCount.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun SpendingTrendSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Spending Trend",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        SubybaraCard(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "[ Spending Trend Line Graph ]",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun UpcomingRenewalsSection(
    renewals: List<Subscription>,
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

        SubybaraCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                renewals.forEachIndexed { index, subscription ->
                    DashboardRenewalRow(
                        subscription = subscription,
                        onClick = { onSelectSubscription(subscription) }
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
    subscription: Subscription,
    onClick: () -> Unit
) {
    val daysRemaining = calculateDaysRemaining(subscription.renewalDate)
    val formattedDate = parseFormattedDate(subscription.renewalDate)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SubscriptionLogo(name = subscription.name)

        Spacer(modifier = Modifier.width(12.dp))

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
                text = "In $daysRemaining days • $formattedDate",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = "₱${String.format(Locale.US, "%,.2f", subscription.amount)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
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

@Composable
private fun SpendingInsightsSection(
    totalMonthlySpend: Double,
    subscriptions: List<Subscription>,
    onViewInsightsClick: () -> Unit
) {
    val topCategory = subscriptions
        .groupBy { it.category.label }
        .maxByOrNull { it.value.sumOf { item -> item.amount } }

    val topCategoryName = topCategory?.key ?: "N/A"
    val topCategoryAmount = topCategory?.value?.sumOf { it.amount } ?: 0.0
    val topCategoryPercentage = if (totalMonthlySpend > 0) {
        ((topCategoryAmount / totalMonthlySpend) * 100).toInt()
    } else {
        0
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Spending Insights",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        SubybaraCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InsightMetricRow(
                    title = "First month tracked",
                    subtitle = "Compared to last month",
                    value = "₱${String.format(Locale.US, "%,.2f", totalMonthlySpend)}",
                    valueSubText = "This month"
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                InsightMetricRow(
                    title = "Top Category",
                    subtitle = topCategoryName,
                    value = "$topCategoryPercentage%",
                    valueSubText = null
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.capy_home_footer),
                    contentDescription = "Capybara footer illustration",
                    modifier = Modifier.size(52.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "You're in control!",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Keep tracking to make smarter subscription choices.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onViewInsightsClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(text = "View Insights", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun InsightMetricRow(
    title: String,
    subtitle: String,
    value: String,
    valueSubText: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (valueSubText != null) {
                Text(
                    text = valueSubText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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

private fun parseFormattedDate(rawDate: String): String {
    return runCatching {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = sdf.parse(rawDate) ?: Date()
        val monthSdf = SimpleDateFormat("MMM d", Locale.US)
        monthSdf.format(date)
    }.getOrDefault("Jul 24")
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    SubybaraTheme {
        HomeScreen(
            subscriptions = SubscriptionStore.subscriptions
        )
    }
}