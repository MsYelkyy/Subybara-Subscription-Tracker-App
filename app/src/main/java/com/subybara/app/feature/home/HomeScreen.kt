package com.subybara.app.feature.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.subybara.app.core.designsystem.component.SubybaraBottomBar
import com.subybara.app.core.designsystem.component.chart.BillsChartStyle
import com.subybara.app.core.designsystem.component.chart.ProjectedBillsChart
import com.subybara.app.core.designsystem.theme.CardBorder
import com.subybara.app.core.designsystem.theme.Neutral500
import com.subybara.app.core.designsystem.theme.Neutral800
import com.subybara.app.core.designsystem.theme.Primary300
import com.subybara.app.core.designsystem.theme.Primary700
import com.subybara.app.core.designsystem.theme.Primary900
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.designsystem.theme.White
import com.subybara.app.core.model.CategoryEstimate
import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.SubscriptionChartData
import com.subybara.app.core.model.UpcomingRenewal
import com.subybara.app.core.model.formatDate
import com.subybara.app.core.model.formatSubscriptionAmount
import com.subybara.app.core.model.renewsInLabel
import com.subybara.app.core.model.subscriptionChartData
import com.subybara.app.core.model.upcomingRenewals
import com.subybara.app.feature.subscriptions.SubscriptionLogo
import com.subybara.app.navigation.SubybaraDestination

/**
 * Dashboard screen displaying spending metrics, upcoming renewal list,
 * and spending insights synced with [SubscriptionStore].
 */
@Composable
fun HomeScreen(
    subscriptions: List<Subscription>,
    modifier: Modifier = Modifier,
    onDestinationClick: (SubybaraDestination) -> Unit = {},
    onAddSubscriptionClick: () -> Unit = {},
    onViewAllRenewalsClick: () -> Unit = {},
    onSelectSubscription: (Subscription) -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    hasUnreadNotifications: Boolean = false,
    chartData: SubscriptionChartData = subscriptions.subscriptionChartData()
) {
    val totalMonthlySpend = chartData.estimatedMonthlyCost
    val totalAnnualSpend = chartData.estimatedAnnualCost
    val activeCount = chartData.activeCount
    val upcomingRenewals = subscriptions.upcomingRenewals().take(5)

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
            item {
                HomeHeader(
                    hasUnreadNotifications = hasUnreadNotifications,
                    onNotificationsClick = onNotificationsClick
                )
            }

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

            item { SpendingTrendSection(chartData) }

            item {
                UpcomingRenewalsSection(
                    renewals = upcomingRenewals,
                    onViewAllClick = onViewAllRenewalsClick,
                    onSelectSubscription = onSelectSubscription
                )
            }

            item {
                SpendingInsightsSection(
                    spendThisMonth = chartData.months.first().amount,
                    topCategory = chartData.categories.firstOrNull { it.share > 0 },
                    onViewInsightsClick = { onDestinationClick(SubybaraDestination.Insights) }
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(
    hasUnreadNotifications: Boolean,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Good Morning!",
                modifier = Modifier.semantics { heading() },
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

        IconButton(onClick = onNotificationsClick) {
            Icon(
                painter = painterResource(
                    if (hasUnreadNotifications) R.drawable.ic_bell_unread else R.drawable.ic_bell
                ),
                contentDescription = if (hasUnreadNotifications) {
                    "Notifications, new renewal reminders"
                } else {
                    "Notifications"
                },
                tint = Color.Unspecified
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
        shape = HomeCardShape,
        color = Primary900,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column {
                    Text(
                        text = "Estimated Monthly Cost",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                    Text(
                        text = formatSubscriptionAmount(monthlySpend),
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Primary700)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Cost estimate",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            HorizontalDivider(color = OverviewDividerColor)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Estimated Annual Cost",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                    Text(
                        text = formatSubscriptionAmount(annualSpend),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        maxLines = 1,
                        autoSize = TextAutoSize.StepBased(
                            minFontSize = 8.sp,
                            maxFontSize = MaterialTheme.typography.titleMedium.fontSize
                        )
                    )
                }

                VerticalDivider(color = OverviewDividerColor)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                ) {
                    Text(
                        text = "Active Subscriptions",
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
private fun SpendingTrendSection(chartData: SubscriptionChartData) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (chartData.activeCount == 0) "Spending Trend" else "Projected Monthly Bills",
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (chartData.activeCount == 0) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).testTag("projection-empty"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(76.dp)
                        .background(Color(0xFFFFF3D8), HomeCardShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(painterResource(R.drawable.ic_trend_bars), contentDescription = null)
                }
                Spacer(Modifier.width(18.dp))
                Column(Modifier.weight(1f)) {
                    Text("Tracking starts here", style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold, color = Neutral800)
                    Spacer(Modifier.height(4.dp))
                    Text("Add your first subscription to see your projected monthly bills.",
                        style = MaterialTheme.typography.bodySmall, color = Neutral500)
                }
            }
        } else {
            ProjectedBillsChart(
                data = chartData,
                style = BillsChartStyle.Line,
                modifier = Modifier.fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun UpcomingRenewalsSection(
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

@Composable
private fun SpendingInsightsSection(
    spendThisMonth: Double,
    topCategory: CategoryEstimate?,
    onViewInsightsClick: () -> Unit
) {
    val topCategoryName = topCategory?.category?.label ?: "N/A"
    val topCategoryPercentage = topCategory?.percentage ?: 0

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Spending Insights",
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = HomeCardGreenBorder, shape = HomeCardShape)
                .padding(horizontal = 12.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InsightMetricRow(
                iconRes = R.drawable.ic_spending_trend,
                title = "Projected bills this month",
                subtitle = "Remaining from today",
                value = formatSubscriptionAmount(spendThisMonth)
            )

            InsightMetricRow(
                iconRes = R.drawable.ic_category_breakdown,
                title = "Top Category",
                subtitle = topCategoryName,
                value = "$topCategoryPercentage%"
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Primary300,
                    shape = HomeCardShape
                ),
            shape = HomeCardShape,
            color = InsightsFooterBackground
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
                        style = MaterialTheme.typography.labelLarge,
                        color = Primary900
                    )
                    Text(
                        text = "Keep tracking to make smarter subscription choices.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Neutral500
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onViewInsightsClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary900,
                        contentColor = White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(text = "View Insights", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun InsightMetricRow(
    @DrawableRes iconRes: Int,
    title: String,
    subtitle: String?,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Neutral800
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral500
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Primary900
        )
    }
}

private val HomeCardShape = RoundedCornerShape(16.dp)
private val HomeCardGreenBorder = Primary300.copy(alpha = 0.5f)
private val OverviewDividerColor = White.copy(alpha = 0.08f)
private val InsightsFooterBackground = Color(0xFFEBF4DF)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    SubybaraTheme {
        HomeScreen(
            subscriptions = SubscriptionStore.subscriptions
        )
    }
}