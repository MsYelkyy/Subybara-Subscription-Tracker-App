package com.subybara.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.subybara.app.core.designsystem.theme.CardBorder
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.designsystem.theme.White

private val CardShape = RoundedCornerShape(20.dp)

/**
 * Shared container with a rounded border and a soft shadow.
 *
 * Used across the app except on the Home screen, which uses
 * different container styling.
 *
 * It only provides the shell; the caller decides the size through
 * [modifier] and what goes inside.
 */
@Composable
fun SubybaraCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .dropShadow(
                shape = CardShape,
                shadow = Shadow(
                    radius = 2.dp,
                    spread = 0.dp,
                    color = Color.Black.copy(alpha = 0.25f),
                    offset = DpOffset(0.dp, 0.5.dp)
                )
            )
            .background(White, CardShape)
            .border(
                width = 1.dp,
                color = CardBorder,
                shape = CardShape
            )
            .padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
private fun SubybaraCardPreview() {
    SubybaraTheme {
        SubybaraCard(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Sample Card")
        }
    }
}
