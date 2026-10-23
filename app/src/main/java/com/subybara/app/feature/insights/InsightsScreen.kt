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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subybara.app.R
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.core.designsystem.component.SubybaraBottomBar
import com.subybara.app.core.designsystem.component.SubybaraCard
import com.subybara.app.core.designsystem.component.SubybaraFilterChip
import com.subybara.app.core.designsystem.component.chart.BillsChartStyle
import com.subybara.app.core.designsystem.component.chart.CategoryStackedBar
import com.subybara.app.core.designsystem.component.chart.ProjectedBillsChart
import com.subybara.app.core.designsystem.component.chart.categoryColor
import com.subybara.app.core.designsystem.theme.CardBorder
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.designsystem.theme.WarmBeige
import com.subybara.app.core.model.CategoryEstimate
import com.subybara.app.core.model.SubscriptionChartData
import com.subybara.app.core.model.formatSubscriptionAmount
import com.subybara.app.core.model.subscriptionChartData
import com.subybara.app.navigation.SubybaraDestination

/**
 * Insights screen displaying subscription spending summaries and nested category breakdowns.
 *
 * @param chartData Shared forecast, the same one Home shows.
 * @param modifier Applied to root layout.
 * @param onDestinationClick Callback for bottom bar navigation taps.
 * @param onAddSubscriptionClick Callback for center add button tap.
 */
@Composable
fun InsightsScreen(
    chartData: SubscriptionChartData,
    modifier: Modifier = Modifier,
    onDestinationClick: (SubybaraDestination) -> Unit = {},
    onAddSubscriptionClick: () -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableStateOf("Overview") }

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

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Overview", "Spending", "Categories").forEach { tab ->
                    SubybaraFilterChip(
                        label = tab,
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                if (selectedTab != "Categories") {
                    item { MonthlySpendingCard(chartData) }
                }
                if (selectedTab != "Spending") {
                    item { SpendingByCategoryCard(chartData.categories) }
                    chartData.categories.firstOrNull { it.share > 0 }?.let { top ->
                        item { TopCategoryCard(top) }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthlySpendingCard(chartData: SubscriptionChartData) {
    var periodMenuOpen by rememberSaveable { mutableStateOf(false) }
    SubybaraCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Estimated Monthly Cost", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(formatSubscriptionAmount(chartData.estimatedMonthlyCost),
                style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Projected Monthly Bills", modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelLarge)
                Box {
                    TextButton(onClick = { periodMenuOpen = true }, modifier = Modifier
                        .height(28.dp).background(WarmBeige, RoundedCornerShape(20.dp)),
                        contentPadding = PaddingValues(horizontal = 10.dp)) {
                        Text("Next 6 Months", style = MaterialTheme.typography.labelSmall)
                        Icon(painterResource(R.drawable.ic_chevron_down), contentDescription = null,
                            modifier = Modifier.size(14.dp))
                    }
                    DropdownMenu(expanded = periodMenuOpen, onDismissRequest = { periodMenuOpen = false }) {
                        DropdownMenuItem(
                            text = { Text("${chartData.months.first().fullLabel} – ${chartData.months.last().fullLabel}") },
                            onClick = { periodMenuOpen = false }
                        )
                    }
                }
            }
            ProjectedBillsChart(data = chartData, style = BillsChartStyle.Bar)
        }
    }
}

@Composable
private fun TopCategoryCard(top: CategoryEstimate) {
    Surface(shape = RoundedCornerShape(20.dp), color = WarmBeige,
        modifier = Modifier.fillMaxWidth().testTag("top-category")) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(40.dp).background(categoryColor(top.category), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center) {
                Text("★", color = Color.White, fontSize = 24.sp)
            }
            Column {
                Text("Top: ${top.category.label}", style = MaterialTheme.typography.labelLarge)
                Text("${top.percentage}% of estimated monthly cost", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun SpendingByCategoryCard(
    categories: List<CategoryEstimate>
) {
    SubybaraCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Estimated Spending by Category",
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (categories.isEmpty()) {
                Text("No active subscriptions to analyze yet.", modifier = Modifier.testTag("category-empty"))
            } else if (categories.none { it.share > 0 }) {
                Text("No estimated monthly cost yet.")
            }
            CategoryStackedBar(categories)

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEach { category ->
                    CategoryLegendTag(label = category.category.label, color = categoryColor(category.category))
                }
            }

            HorizontalDivider(color = CardBorder)

            // Category progress rows nested directly inside the card surface
            Column {
                categories.forEachIndexed { index, category ->
                    CategoryProgressRow(
                        category = category,
                        isLast = index == categories.lastIndex
                    )
                }
            }
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
    category: CategoryEstimate,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    val progress = category.share.toFloat()
    val color = categoryColor(category.category)

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
                        .background(WarmBeige)
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
                    text = "${category.category.label} · ${category.percentage}%",
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = formatSubscriptionAmount(category.monthlyAmount),
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
                trackColor = WarmBeige,
                gapSize = 0.dp,
                drawStopIndicator = {}
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun InsightsScreenPreview() {
    SubybaraTheme {
        InsightsScreen(
            chartData = SubscriptionStore.subscriptions.subscriptionChartData()
        )
    }
}
