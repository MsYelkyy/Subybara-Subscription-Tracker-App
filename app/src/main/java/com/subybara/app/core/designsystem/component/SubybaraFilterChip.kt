package com.subybara.app.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subybara.app.core.designsystem.theme.Neutral800
import com.subybara.app.core.designsystem.theme.Primary600
import com.subybara.app.core.designsystem.theme.Secondary200
import com.subybara.app.core.designsystem.theme.Secondary300
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.designsystem.theme.White

/**
 * Filter chip shown above a list, such as the status filter on My Subscription
 * and the date filter on Renewals.
 */
@Composable
fun SubybaraFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = label) },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Secondary200,
            labelColor = Neutral800,
            selectedContainerColor = Primary600,
            selectedLabelColor = White
        ),
        border = BorderStroke(0.3.dp, Secondary300)
    )
}

@Preview(showBackground = true)
@Composable
private fun SubybaraFilterChipPreview() {
    SubybaraTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SubybaraFilterChip(label = "All", selected = true, onClick = {})
            SubybaraFilterChip(label = "Active", selected = false, onClick = {})
        }
    }
}
