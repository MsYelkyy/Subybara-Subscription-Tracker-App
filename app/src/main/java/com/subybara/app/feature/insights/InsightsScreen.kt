package com.subybara.app.feature.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
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

private val CategoryPalette = listOf(
    Color(0xFF6B8E4E),
    Color(0xFFE0A34A),
    Color(0xFFC85A32),
    Color(0xFF9D7BB0),
    Color(0xFF7FA3C9),
    Color(0xFF8DAA9D)
)

/**
 * Insights screen displaying subscription spending summaries and nested category breakdowns.
 *
 * @param subscriptions List of user subscriptions, defaults to [SubscriptionStore.subscriptions].
 * @param modifier Applied to root layout.
 * @param onDestinationClick Callback for bottom bar navigation taps.
 * @param onAddSubscriptionClick Callback for center add button tap.
 */
@Composable
fun InsightsScreen(
    subscriptions: List<Subscription> = SubscriptionStore.subscriptions,
    modifier: Modifier = Modifier,
    onDestinationClick: (SubybaraDestination) -> Unit = {},
    onAddSubscriptionClick: () -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableStateOf(InsightsTab.Overview) }

    val totalMonthlySpend = subscriptions.sumOf { it.amount }

    val categorySummaries = subscriptions
        .groupBy { it.category.label }
        .entries
        .mapIndexed { index, (label, items) ->
            Triple(label, items.sumOf { it.amount }, CategoryPalette[index % CategoryPalette.size])
        }
        .sortedByDescending { it.second }

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

            InsightsTabRow(
                selectedTab = selectedTab,
                onTabSelect = { selectedTab = it }
            )

            if (subscriptions.isEmpty()) {
                EmptyInsightsMessage()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        MonthlySpendingCard(
                            totalSpend = totalMonthlySpend
                        )
                    }

                    item {
                        SpendingByCategoryCard(
                            categories = categorySummaries,
                            totalSpend = totalMonthlySpend
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightsTabRow(
    selectedTab: InsightsTab,
    onTabSelect: (InsightsTab) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        InsightsTab.entries.forEach { tab ->
            FilterChip(
                selected = tab == selectedTab,
                onClick = { onTabSelect(tab) },
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
}

@Composable
private fun MonthlySpendingCard(
    totalSpend: Double
) {
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
                text = "₱${String.format(Locale.US, "%,.2f", totalSpend)}",
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
                    text = "First month tracked",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SpendingByCategoryCard(
    categories: List<Triple<String, Double, Color>>,
    totalSpend: Double
) {
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

            SegmentedCategoryBar(categories = categories, totalSpend = totalSpend)

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEach { (categoryName, _, color) ->
                    CategoryLegendTag(label = categoryName, color = color)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Category progress rows nested directly inside the card surface
            categories.forEachIndexed { index, (categoryName, totalAmount, color) ->
                CategoryProgressRow(
                    categoryName = categoryName,
                    totalAmount = totalAmount,
                    color = color,
                    totalSpend = totalSpend,
                    showDivider = index < categories.lastIndex
                )
            }
        }
    }
}

@Composable
private fun SegmentedCategoryBar(
    categories: List<Triple<String, Double, Color>>,
    totalSpend: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(RoundedCornerShape(8.dp)),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        categories.forEach { (_, amount, color) ->
            val weight = if (totalSpend > 0) (amount / totalSpend).toFloat() else 1f
            Box(
                modifier = Modifier
                    .weight(weight.coerceAtLeast(0.01f))
                    .fillMaxHeight()
                    .background(color)
            )
        }
    }
}

@Composable
private fun CategoryLegendTag(
    label: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CategoryProgressRow(
    categoryName: String,
    totalAmount: Double,
    color: Color,
    totalSpend: Double,
    showDivider: Boolean,
    modifier: Modifier = Modifier
) {
    val progress = if (totalSpend > 0) (totalAmount / totalSpend).toFloat() else 0f

    Column(
        modifier = modifier.fillMaxWidth(),
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
                        .background(color)
                )
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "₱${String.format(Locale.US, "%,.2f", totalAmount)}",
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
            color = color,
            trackColor = MaterialTheme.colorScheme.secondary
        )

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                thickness = 0.5.dp
            )
        }
    }
}

@Composable
private fun EmptyInsightsMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No subscription data available.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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