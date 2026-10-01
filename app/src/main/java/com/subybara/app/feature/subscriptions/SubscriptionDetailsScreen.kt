package com.subybara.app.feature.subscriptions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subybara.app.R
import com.subybara.app.core.designsystem.theme.SubybaraTheme

@Composable
fun SubscriptionDetailsScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
    ) { innerPadding ->

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 24.dp
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_long_left),
                contentDescription = "Back",
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = painterResource(R.drawable.ic_edit_subscription),
                contentDescription = "Edit subscription",
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(R.drawable.logo_netflix),
                contentDescription = "Netflix logo",
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun SubscriptionDetailsScreenPreview() {
    SubybaraTheme {
        SubscriptionDetailsScreen()
    }
}