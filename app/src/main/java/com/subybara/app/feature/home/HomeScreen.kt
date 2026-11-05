package com.subybara.app.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subybara.app.R
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.core.designsystem.component.SubybaraBottomBar
import com.subybara.app.core.designsystem.theme.Primary300
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.designsystem.theme.White
import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.SubscriptionChartData
import com.subybara.app.core.model.subscriptionChartData
import com.subybara.app.core.model.upcomingRenewals
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

internal val HomeCardShape = RoundedCornerShape(16.dp)
internal val HomeCardGreenBorder = Primary300.copy(alpha = 0.5f)
internal val OverviewDividerColor = White.copy(alpha = 0.08f)
internal val InsightsFooterBackground = Color(0xFFEBF4DF)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    SubybaraTheme {
        HomeScreen(
            subscriptions = SubscriptionStore.subscriptions
        )
    }
}
