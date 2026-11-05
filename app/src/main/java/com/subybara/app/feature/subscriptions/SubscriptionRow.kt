package com.subybara.app.feature.subscriptions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.subybara.app.R
import com.subybara.app.core.designsystem.theme.Neutral100
import com.subybara.app.core.designsystem.theme.Neutral500
import com.subybara.app.core.designsystem.theme.Neutral800
import com.subybara.app.core.designsystem.theme.Primary900
import com.subybara.app.core.model.Subscription
import com.subybara.app.core.model.formatDate
import com.subybara.app.core.model.formatSubscriptionAmount
import com.subybara.app.core.model.nextBillingDate

private val SubscriptionRowPressedColor = Neutral100.copy(alpha = 0.6f)

@Composable
internal fun SubscriptionRow(
    subscription: Subscription,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isPressed) SubscriptionRowPressedColor else Color.Transparent)
            .reportPressesTo(interactionSource)
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SubscriptionLogo(
            name = subscription.name,
            logoUri = subscription.logoUri
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = subscription.name,
                style = MaterialTheme.typography.labelLarge,
                color = Neutral800,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subscription.category.label,
                style = MaterialTheme.typography.bodySmall


            )

            val billingDate = subscription.nextBillingDate()
                ?.let { date -> formatDate(date, "MMMM d") }
                ?: subscription.renewalDate

            Text(
                text = "Renews on $billingDate",
                style = MaterialTheme.typography.bodySmall,
                color = Neutral500
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = formatSubscriptionAmount(subscription.amount),
                style = MaterialTheme.typography.labelMedium,
                color = Primary900
            )

            Text(
                text = subscription.billingCycle,
                style = MaterialTheme.typography.bodySmall,
                color = Neutral500
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.Unspecified
        )
    }
}

/**
 * Shows press feedback immediately, including quick taps in a scrolling list.
 */
private fun Modifier.reportPressesTo(
    interactionSource: MutableInteractionSource
): Modifier = pointerInput(interactionSource) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val press = PressInteraction.Press(down.position)
        interactionSource.tryEmit(press)

        // Null means the touch was cancelled, such as when scrolling.
        val up = waitForUpOrCancellation()
        interactionSource.tryEmit(
            if (up != null) PressInteraction.Release(press) else PressInteraction.Cancel(press)
        )
    }
}
