package com.subybara.app.feature.subscriptions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.subybara.app.R
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.core.designsystem.theme.SubybaraTheme
import com.subybara.app.core.model.Subscription

/**
 * Edits an existing subscription.
 *
 * Calls [onSaveClick] after the user confirms the changes.
 *
 * @param subscription Subscription being edited.
 * @param onCloseClick Called when the user leaves the form without saving.
 * @param onSaveClick Called with the updated subscription once the user confirms.
 * @param modifier Modifier applied to the screen.
 */
@Composable
fun EditSubscriptionScreen(
    subscription: Subscription,
    onCloseClick: () -> Unit,
    onSaveClick: (Subscription) -> Unit,
    modifier: Modifier = Modifier
) {
    var changesToSave by remember { mutableStateOf<Subscription?>(null) }

    SubscriptionForm(
        title = "Edit Subscription",
        headerImage = R.drawable.capy_edit_subscription,
        initialSubscription = subscription,
        onCloseClick = onCloseClick,
        onSaveClick = { updatedSubscription ->
            changesToSave = updatedSubscription
        },
        modifier = modifier
    )

    if (changesToSave != null) {
        SaveChangesDialog(
            onYesClick = {
                // Prevent duplicate saves on repeated taps.
                changesToSave?.let { updatedSubscription ->
                    changesToSave = null
                    onSaveClick(updatedSubscription)
                }
            },
            onNoClick = { changesToSave = null }
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun EditSubscriptionScreenPreview() {
    SubybaraTheme {
        EditSubscriptionScreen(
            subscription = SubscriptionStore.subscriptions.first { subscription ->
                subscription.name == "Netflix"
            },
            onCloseClick = {},
            onSaveClick = {}
        )
    }
}
