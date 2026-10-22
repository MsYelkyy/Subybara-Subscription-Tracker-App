package com.subybara.app.core.designsystem.component.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subybara.app.core.designsystem.theme.Primary300
import com.subybara.app.core.designsystem.theme.Primary600
import com.subybara.app.core.designsystem.theme.Primary900
import com.subybara.app.core.model.SubscriptionChartData
import com.subybara.app.core.model.formatSubscriptionAmount
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

enum class BillsChartStyle { Line, Bar }

/** Shared axes and accessible month values; each screen supplies the same forecast. */
@Composable
fun ProjectedBillsChart(
    data: SubscriptionChartData,
    style: BillsChartStyle,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        if (data.activeCount == 0) {
            Text(
                "No active subscriptions to project yet.",
                modifier = Modifier.padding(vertical = 36.dp).testTag("projection-empty"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            val measurer = rememberTextMeasurer()
            val labelStyle = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
            val labelColor = if (style == BillsChartStyle.Bar) Primary600
                else MaterialTheme.colorScheme.onSurfaceVariant
            val months = data.months
            val maximum = months.maxOfOrNull { it.amount } ?: 0.0
            val magnitude = 10.0.pow(floor(log10(maximum.coerceAtLeast(1.0))))
            val axisMax = (ceil(maximum / magnitude) * magnitude).coerceAtLeast(1.0)
            val description = "Projected Monthly Bills. " + months.joinToString("; ") {
                "${it.fullLabel}: ${formatSubscriptionAmount(it.amount)}"
            }
            Canvas(
                Modifier.fillMaxWidth().height(if (style == BillsChartStyle.Line) 220.dp else 175.dp)
                    .testTag(if (style == BillsChartStyle.Line) "projection-line" else "projection-bar")
                    .semantics { contentDescription = description }
            ) {
                fun label(value: String) = measurer.measure(value, labelStyle)
                val ticks = (0..4).map { axisMax * it / 4 }
                fun tickLabel(value: Double): String = when {
                    value >= 1000 -> "${"%.1f".format(java.util.Locale.US, value / 1000).removeSuffix(".0")}K"
                    else -> java.text.DecimalFormat("#,##0.##",
                        java.text.DecimalFormatSymbols(java.util.Locale.US)).format(value)
                }
                val left = ticks.maxOf { label(tickLabel(it)).size.width }.toFloat() + 8.dp.toPx()
                val right = size.width - 14.dp.toPx()
                val top = 32.dp.toPx()
                val bottom = size.height - label("Sep").size.height - 10.dp.toPx()
                val height = (bottom - top).coerceAtLeast(1f)
                val width = (right - left).coerceAtLeast(1f)
                ticks.forEach { tick ->
                    val y = bottom - (tick / axisMax).toFloat() * height
                    drawLine(Color(0xFFE4E4DC), Offset(left, y), Offset(right, y), 1.dp.toPx())
                    val text = label(tickLabel(tick))
                    drawText(text, labelColor, Offset(left - text.size.width - 6.dp.toPx(), y - text.size.height / 2))
                }
                val slot = width / months.size
                val points = months.mapIndexed { index, month ->
                    val x = if (style == BillsChartStyle.Bar) left + slot * (index + 0.5f)
                        else left + width * index / (months.size - 1).coerceAtLeast(1)
                    val y = bottom - (month.amount / axisMax).toFloat() * height
                    val text = label(month.label)
                    drawText(text, labelColor, Offset(x - text.size.width / 2, bottom + 6.dp.toPx()))
                    Offset(x, y)
                }
                if (style == BillsChartStyle.Line) {
                    val path = Path().apply {
                        points.forEachIndexed { index, point ->
                            if (index == 0) moveTo(point.x, point.y) else lineTo(point.x, point.y)
                        }
                    }
                    drawPath(path, Primary900, style = Stroke(1.5.dp.toPx()))
                    points.forEach { drawCircle(Primary900, 2.5.dp.toPx(), it, style = Stroke(1.dp.toPx())) }
                    points.lastOrNull()?.let { point ->
                        val text = label(formatSubscriptionAmount(months.last().amount))
                        val boxWidth = text.size.width + 12.dp.toPx()
                        val boxHeight = text.size.height + 12.dp.toPx()
                        val x = (point.x - boxWidth / 2).coerceIn(0f, (size.width - boxWidth).coerceAtLeast(0f))
                        val y = (point.y - boxHeight - 8.dp.toPx()).coerceAtLeast(0f)
                        drawRoundRect(Primary900, Offset(x, y), Size(boxWidth, boxHeight), CornerRadius(10.dp.toPx()))
                        drawText(text, Color.White, Offset(x + 6.dp.toPx(), y + 6.dp.toPx()))
                    }
                } else {
                    points.forEachIndexed { index, point ->
                        if (months[index].amount <= 0) return@forEachIndexed
                        val half = slot * 0.42f
                        val radius = minOf(5.dp.toPx(), (bottom - point.y) / 2, half)
                        val path = Path().apply {
                            moveTo(point.x - half, bottom)
                            lineTo(point.x - half, point.y + radius)
                            quadraticTo(point.x - half, point.y, point.x - half + radius, point.y)
                            lineTo(point.x + half - radius, point.y)
                            quadraticTo(point.x + half, point.y, point.x + half, point.y + radius)
                            lineTo(point.x + half, bottom)
                            close()
                        }
                        drawPath(path, Brush.verticalGradient(
                            listOf(if (index == points.lastIndex) Primary600 else Primary300, Primary300.copy(alpha = 0.55f)),
                            startY = point.y, endY = bottom
                        ))
                    }
                }
            }
        }
        if (data.invalidRenewalCount > 0) {
            Text("${data.invalidRenewalCount} active subscriptions need a valid renewal date.",
                style = MaterialTheme.typography.bodySmall)
        }
    }
}
