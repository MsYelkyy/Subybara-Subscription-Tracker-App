package com.subybara.app.feature.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subybara.app.R
import com.subybara.app.core.designsystem.theme.Neutral500
import com.subybara.app.core.designsystem.theme.Neutral800
import com.subybara.app.core.designsystem.theme.Primary300
import com.subybara.app.core.designsystem.theme.Primary900
import com.subybara.app.core.designsystem.theme.White
import com.subybara.app.core.model.CategoryEstimate
import com.subybara.app.core.model.formatSubscriptionAmount

@Composable
internal fun SpendingInsightsSection(
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
