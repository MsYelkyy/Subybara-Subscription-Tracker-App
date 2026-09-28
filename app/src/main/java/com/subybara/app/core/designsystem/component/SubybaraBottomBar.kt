package com.subybara.app.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subybara.app.R
import com.subybara.app.core.designsystem.theme.SubybaraForestGreen
import com.subybara.app.core.designsystem.theme.SubybaraOffWhite
import com.subybara.app.core.designsystem.theme.SubybaraSageGreen
import com.subybara.app.core.designsystem.theme.SubybaraSoftGreen
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.navigation.SubybaraDestination

private val BottomBarShape = RoundedCornerShape(
    topStart = 0.dp,
    topEnd = 0.dp,
    bottomStart = 20.dp,
    bottomEnd = 20.dp
)
private val BottomBarShadow = Shadow(
    radius = 12.dp,
    color = SubybaraForestGreen.copy(alpha = 0.12f),
    offset = DpOffset(x = 0.dp, y = (-2).dp)
)

/**
 * Shared bottom navigation bar for the four main screens:
 * Home, Subscriptions, Insights and Profile.
 *
 * The center button is an action (Add Subscription), not a destination,
 * so it is never shown as selected.
 *
 * @param selectedDestination The main screen currently shown.
 * @param onDestinationClick Called with the tapped main destination.
 * @param onAddSubscriptionClick Called when the center add button is tapped.
 * @param modifier Modifier applied to the bar.
 */
@Composable
fun SubybaraBottomBar(
    selectedDestination: SubybaraDestination,
    onDestinationClick: (SubybaraDestination) -> Unit,
    onAddSubscriptionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(shape = BottomBarShape, shadow = BottomBarShadow)
            .background(SubybaraOffWhite, BottomBarShape)
            .navigationBarsPadding()
            .height(66.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SubybaraNavigationItem(
            iconRes = R.drawable.ic_home,
            label = "Home",
            selected = selectedDestination == SubybaraDestination.Home,
            onClick = { onDestinationClick(SubybaraDestination.Home) },
            modifier = Modifier.weight(1f)
        )
        SubybaraNavigationItem(
            iconRes = R.drawable.ic_subscriptions,
            label = "Subscriptions",
            selected = selectedDestination == SubybaraDestination.Subscriptions,
            onClick = { onDestinationClick(SubybaraDestination.Subscriptions) },
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_subscription),
                contentDescription = "Add subscription",
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = onAddSubscriptionClick),
                tint = Color.Unspecified
            )
        }
        SubybaraNavigationItem(
            iconRes = R.drawable.ic_insights,
            label = "Insights",
            selected = selectedDestination == SubybaraDestination.Insights,
            onClick = { onDestinationClick(SubybaraDestination.Insights) },
            modifier = Modifier.weight(1f)
        )
        SubybaraNavigationItem(
            iconRes = R.drawable.ic_profile,
            label = "Profile",
            selected = selectedDestination == SubybaraDestination.Profile,
            onClick = { onDestinationClick(SubybaraDestination.Profile) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SubybaraNavigationItem(
    @DrawableRes iconRes: Int,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = if (selected) SubybaraForestGreen else SubybaraSageGreen

    Column(
        modifier = modifier
            .fillMaxHeight()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.background(
                color = if (selected) SubybaraSoftGreen else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                // Inactive icons keep the color drawn in the vector asset.
                tint = if (selected) SubybaraForestGreen else Color.Unspecified
            )
        }
        Text(
            text = label,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SubybaraBottomBarPreview() {
    SubybaraTheme {
        SubybaraBottomBar(
            selectedDestination = SubybaraDestination.Home,
            onDestinationClick = {},
            onAddSubscriptionClick = {}
        )
    }
}
