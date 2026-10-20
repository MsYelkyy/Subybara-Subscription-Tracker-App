package com.subybara.app.feature.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.core.designsystem.component.SubybaraBottomBar
import com.subybara.app.core.designsystem.component.SubybaraCard
import com.subybara.app.core.designsystem.theme.CardBorder
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.formatSubscriptionAmount
import com.subybara.app.core.model.spendByCategory
import com.subybara.app.core.model.spendDueThisMonth
import com.subybara.app.core.model.totalMonthlySpend
import com.subybara.app.navigation.SubybaraDestination

private val CategoryPalette = listOf(
    Color(0xFF7E9B63),
    Color(0xFFD7A44E),
    Color(0xFFBF683A),
    Color(0xFF9F7EC1),
    Color(0xFF89AED0),
    Color(0xFF8DAA9D)
)

private val CategoryTrackColor = Color(0xFFF1EDDC)

/**
 * Insights screen displaying subscription spending summaries and nested category breakdowns.
 *
 * @param subscriptions List of user subscriptions, usually [SubscriptionStore.subscriptions].
 * @param modifier Applied to root layout.
 * @param onDestinationClick Callback for bottom bar navigation taps.
 * @param onAddSubscriptionClick Callback for center add button tap.
 */
@Composable
fun InsightsScreen(
    subscriptions: List<Subscription>,
    modifier: Modifier = Modifier,
    onDestinationClick: (SubybaraDestination) -> Unit = {},
    onAddSubscriptionClick: () -> Unit = {}
) {
    val totalMonthlySpend = subscriptions.totalMonthlySpend()
    val spendThisMonth = subscriptions.spendDueThisMonth()

    val categorySummaries = subscriptions
        .spendByCategory()
        .mapIndexed { index, categorySpend ->
            Triple(
                categorySpend.category.label,
                categorySpend.monthlyAmount,
                CategoryPalette[index % CategoryPalette.size]
            )
        }

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
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { heading() },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (categorySummaries.isEmpty()) {
                EmptyInsightsMessage()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        MonthlySpendingCard(
                            totalSpend = spendThisMonth
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
private fun MonthlySpendingCard(
    totalSpend: Double
) {
    SubybaraCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "ESTIMATED BILLS THIS MONTH",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = formatSubscriptionAmount(totalSpend),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Based on billing dates. Payments aren't tracked.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                modifier = Modifier.semantics { heading() },
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

            HorizontalDivider(color = CardBorder)

            // Category progress rows nested directly inside the card surface
            Column {
                categories.forEachIndexed { index, (categoryName, totalAmount, color) ->
                    CategoryProgressRow(
                        categoryName = categoryName,
                        totalAmount = totalAmount,
                        color = color,
                        totalSpend = totalSpend,
                        isLast = index == categories.lastIndex
                    )
                }
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
            .height(20.dp)
            .clip(RoundedCornerShape(10.dp)),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
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
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    val progress = if (totalSpend > 0) (totalAmount / totalSpend).toFloat() else 0f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Column(
            modifier = Modifier
                .width(16.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp, bottom = 4.dp)
                        .width(2.dp)
                        .weight(1f)
                        .background(CategoryTrackColor)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = categoryName,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = formatSubscriptionAmount(totalAmount),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = color,
                trackColor = CategoryTrackColor,
                gapSize = 0.dp,
                drawStopIndicator = {}
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
            text = "No active subscriptions to analyze yet.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
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