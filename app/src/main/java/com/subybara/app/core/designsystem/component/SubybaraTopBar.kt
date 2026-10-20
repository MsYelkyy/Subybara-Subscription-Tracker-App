package com.subybara.app.core.designsystem.component

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.subybara.app.R
import com.subybara.app.core.designsystem.theme.Neutral800
import com.subybara.app.core.designsystem.theme.SubybaraTheme

/**
 * Top bar with a centered title and a back arrow, used by screens that are
 * opened from another screen, such as Renewals and Notifications.
 *
 * [backContentDescription] is read by screen readers for the back arrow.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubybaraTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    backContentDescription: String = "Back"
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.titleLarge,
                color = Neutral800
            )
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_long_left),
                    contentDescription = backContentDescription,
                    tint = Neutral800
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun SubybaraTopBarPreview() {
    SubybaraTheme {
        SubybaraTopBar(title = "Renewals", onBackClick = {})
    }
}
