package com.subybara.app.core.designsystem.component.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.subybara.app.core.designsystem.theme.WarmBeige
import com.subybara.app.core.model.CategoryEstimate
import com.subybara.app.core.model.SubscriptionCategory

fun categoryColor(category: SubscriptionCategory): Color = when (category) {
    SubscriptionCategory.ENTERTAINMENT -> Color(0xFF7E9B63)
    SubscriptionCategory.PRODUCTIVITY -> Color(0xFFD7A44E)
    SubscriptionCategory.MUSIC -> Color(0xFFBF683A)
    SubscriptionCategory.CLOUD_STORAGE -> Color(0xFF9F7EC1)
    SubscriptionCategory.OTHERS -> Color(0xFF89AED0)
    SubscriptionCategory.EDUCATION -> Color(0xFF8DAA9D)
    SubscriptionCategory.GAMING -> Color(0xFFAD8490)
    SubscriptionCategory.HEALTH_FITNESS -> Color(0xFF579A8A)
    SubscriptionCategory.NEWS_READING -> Color(0xFFB4946E)
    SubscriptionCategory.UTILITIES -> Color(0xFF7688A1)
}

@Composable
fun CategoryStackedBar(categories: List<CategoryEstimate>, modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxWidth().height(20.dp).clip(RoundedCornerShape(10.dp))
        .testTag("category-stacked-bar").semantics {
            contentDescription = categories.joinToString("; ") { "${it.category.label}: ${it.percentage}%" }
        }) {
        drawRect(WarmBeige)
        var x = 0f
        categories.filter { it.share > 0 }.forEachIndexed { index, category ->
            val width = size.width * category.share.toFloat()
            drawRect(categoryColor(category.category), Offset(x, 0f), Size(width, size.height))
            if (index > 0) drawLine(Color.White, Offset(x, 0f), Offset(x, size.height), 1.dp.toPx())
            x += width
        }
    }
}
