package com.subybara.app.feature.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.core.designsystem.component.SubybaraBottomBar
import com.subybara.app.core.designsystem.component.SubybaraCard
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.model.Subscription
import com.subybara.app.navigation.SubybaraDestination
import java.util.Locale


// local sample data set -- edit values here for graph and bar

data class MonthlyTrendData(
    val month: String,
    val amount: Double,
    val isCurrentMonth: Boolean = false
)

data class CategorySpendingData(
    val categoryName: String,
    val amount: Double,
    val color: Color
)

private val SampleMonthlyTrends = listOf(
    MonthlyTrendData("Dec", 5800.0),
    MonthlyTrendData("Jan", 8200.0),
    MonthlyTrendData("Feb", 6900.0),
    MonthlyTrendData("Mar", 11200.0),
    MonthlyTrendData("Apr", 9400.0),
    MonthlyTrendData("May", 9786.95, isCurrentMonth = true)
)

private val SampleCategorySpending = listOf(
    CategorySpendingData("Entertainment", 4404.13, Color(0xFF6B8E4E)),
    CategorySpendingData("Productivity", 1957.39, Color(0xFFE0A34A)),
    CategorySpendingData("Music", 1500.00, Color(0xFFC85A32)),
    CategorySpendingData("Cloud Storage", 1000.00, Color(0xFF9D7BB0)),
    CategorySpendingData("Others", 925.43, Color(0xFF7FA3C9))
)

/**
 * Insights screen displaying subscription spending summaries, bar chart trends,
 * segmented category bars, and category breakdowns.
 *
 * @param subscriptions List of user subscriptions, defaults to [SubscriptionStore.subscriptions].
 * @param modifier Applied to root layout.
 * @param onDestinationClick Callback for bottom bar navigation taps.
 * @param onAddSubscriptionClick Callback for center add button tap.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InsightsScreen(
    subscriptions: List<Subscription> = SubscriptionStore.subscriptions,
    modifier: Modifier = Modifier,
    onDestinationClick: (SubybaraDestination) -> Unit = {},
    onAddSubscriptionClick: () -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableStateOf(InsightsTab.Overview) }

    val currentSpend = SampleMonthlyTrends.lastOrNull()?.amount ?: 0.0
    val totalCategorySpend = SampleCategorySpending.sumOf { it.amount }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            SubybaraBottomBar(
                selectedDestination = SubybaraDestination.Insights,
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
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Insights",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sub-tabs: Overview, Spending,Categories
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                InsightsTab.entries.forEach { tab ->
                    val isSelected = tab == selectedTab
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        label = { Text(text = tab.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface,
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Monthly Spending & Bar Chart Card
                item {
                    SubybaraCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "THIS MONTH SPENDING",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "₱${String.format(Locale.US, "%,.2f", currentSpend)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "+ 12% vs last month",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Trend Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Trend",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Last 6 months ⌄",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Bar Chart
                            BarChartSection(trends = SampleMonthlyTrends)
                        }
                    }
                }

                // Spending by Category Section
                item {
                    SubybaraCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Spending by Category",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Segmented Bar
                            SegmentedCategoryBar(
                                categories = SampleCategorySpending,
                                totalSpend = totalCategorySpend
                            )

                            // Legend Tags
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                SampleCategorySpending.forEach { category ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(category.color)
                                        )
                                        Text(
                                            text = category.categoryName,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Detailed Category List
                items(SampleCategorySpending) { category ->
                    CategoryProgressCard(
                        category = category,
                        totalSpend = totalCategorySpend
                    )
                }
            }
        }
    }
}

// ============================================================================
// HELPER COMPOSABLES FOR GRAPH & BARS
// ============================================================================

@Composable
private fun BarChartSection(trends: List<MonthlyTrendData>) {
    val maxAmount = (trends.maxOfOrNull { it.amount } ?: 1.0) * 1.15

    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        // Y-Axis Labels & Bars Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("15K", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                Text("10K", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                Text("5K", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                Text("0", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
            }

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                trends.forEach { trend ->
                    val heightFactor = (trend.amount / maxAmount).toFloat().coerceIn(0.1f, 1f)
                    val barColor = if (trend.isCurrentMonth) Color(0xFF6B8E4E) else Color(0xFFCBE3A9)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .fillMaxHeight(heightFactor)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 2.dp, bottomEnd = 2.dp))
                            .background(barColor)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // X-Axis Month Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            trends.forEach { trend ->
                Text(
                    text = trend.month,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun SegmentedCategoryBar(
    categories: List<CategorySpendingData>,
    totalSpend: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(RoundedCornerShape(8.dp)),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        categories.forEach { category ->
            val weight = if (totalSpend > 0) (category.amount / totalSpend).toFloat() else 1f
            Box(
                modifier = Modifier
                    .weight(weight.coerceAtLeast(0.01f))
                    .fillMaxHeight()
                    .background(category.color)
            )
        }
    }
}

@Composable
private fun CategoryProgressCard(
    category: CategorySpendingData,
    totalSpend: Double
) {
    val progress = if (totalSpend > 0) (category.amount / totalSpend).toFloat() else 0f

    SubybaraCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(category.color)
                    )
                    Text(
                        text = category.categoryName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "₱${String.format(Locale.US, "%,.2f", category.amount)}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = category.color,
                trackColor = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

private enum class InsightsTab(val label: String) {
    Overview("Overview"),
    Spending("Spending"),
    Categories("Categories")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun InsightsScreenPreview() {
    SubybaraTheme {
        InsightsScreen(
            subscriptions = SubscriptionStore.subscriptions
        )
    }
}