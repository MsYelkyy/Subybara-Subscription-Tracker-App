package com.subybara.app.core.designsystem.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subybara.app.core.designsystem.theme.SubybaraLeafGreen
import com.subybara.app.core.designsystem.theme.SubybaraTheme

private val CardShape = RoundedCornerShape(20.dp)

/**
 * Shared transparent container with a rounded border and a soft shadow.
 *
 * It only provides the shell; the caller decides the size (through
 * [modifier]) and what goes inside.
 */
@Composable
fun SubybaraCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .cardShadow()
            .border(width = 0.5.dp, color = SubybaraLeafGreen, shape = CardShape)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        content = content
    )
}

/**
 * Figma shadow: x 0, y 1, blur 1, spread 0.
 */
private fun Modifier.cardShadow(): Modifier = drawBehind {
    val outline = CardShape.createOutline(size, layoutDirection, this)
    val cardArea = Path().apply { addOutline(outline) }

    clipPath(cardArea, clipOp = ClipOp.Difference) {
        translate(top = 1.dp.toPx()) {
            drawOutline(outline, color = SubybaraLeafGreen)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SubybaraCardPreview() {
    SubybaraTheme {
        SubybaraCard(modifier = Modifier.padding(16.dp)) {
            Text(text = "Sample Card")
        }
    }
}
