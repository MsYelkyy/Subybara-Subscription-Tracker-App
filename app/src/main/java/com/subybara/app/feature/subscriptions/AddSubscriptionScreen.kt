package com.subybara.app.feature.subscriptions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.subybara.app.R
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.model.Subscription

/**
 * Adds a new subscription.
 *
 * Shows a success dialog after [onSaveClick] saves the subscription.
 *
 * @param onCloseClick Called when the user leaves the form without saving.
 * @param onSaveClick Called with the new subscription when the user taps Save.
 * @param onDoneClick Called when the user taps Done on the success dialog.
 * @param modifier Modifier applied to the screen.
 */
@Composable
fun AddSubscriptionScreen(
    onCloseClick: () -> Unit,
    onSaveClick: (Subscription) -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var addedSubscriptionName by rememberSaveable { mutableStateOf<String?>(null) }

    SubscriptionForm(
        title = "Add Subscription",
        headerImage = R.drawable.capy_add_subscription,
        initialSubscription = null,
        onCloseClick = onCloseClick,
        onSaveClick = { subscription ->
            // Prevent duplicate subscriptions on repeated taps.
            if (addedSubscriptionName == null) {
                onSaveClick(subscription)
                addedSubscriptionName = subscription.name
            }
        },
        modifier = modifier
    )

    addedSubscriptionName?.let { subscriptionName ->
        SubscriptionAddedDialog(
            subscriptionName = subscriptionName,
            onDoneClick = onDoneClick
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun AddSubscriptionScreenPreview() {
    SubybaraTheme {
        AddSubscriptionScreen(
            onCloseClick = {},
            onSaveClick = {},
            onDoneClick = {}
        )
    }
}
