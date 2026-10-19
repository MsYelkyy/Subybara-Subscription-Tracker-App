package com.subybara.app.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.feature.account.WelcomeScreen
import com.subybara.app.feature.home.HomeScreen
import com.subybara.app.feature.subscriptions.AddSubscriptionScreen
import com.subybara.app.feature.subscriptions.EditSubscriptionScreen
import com.subybara.app.feature.subscriptions.SubscriptionDetailsScreen
import com.subybara.app.feature.subscriptions.SubscriptionSnackbarHost
import com.subybara.app.feature.subscriptions.SubscriptionsScreen
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

@Composable
fun SubybaraNavHost() {
    var currentRoute by rememberSaveable { mutableStateOf(SubybaraDestination.Welcome.route) }
    var selectedSubscriptionId by rememberSaveable { mutableIntStateOf(0) }
    var addSubscriptionOrigin by rememberSaveable {
        mutableStateOf(SubybaraDestination.Subscriptions.route)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val navigator = DestinationNavigator { destination -> currentRoute = destination.route }

    val onMainDestinationClick: (SubybaraDestination) -> Unit = { destination ->
        if (destination == SubybaraDestination.Home ||
            destination == SubybaraDestination.Subscriptions
        ) {
            navigator.navigateTo(destination)
        }
    }

    // Remember where the add button was tapped so closing the form returns there.
    val onAddSubscriptionClick: () -> Unit = {
        addSubscriptionOrigin = currentRoute
        navigator.navigateTo(SubybaraDestination.AddSubscription)
    }

    // Replace the current message so repeated taps don't queue old snackbars.
    var feedbackJob by remember { mutableStateOf<Job?>(null) }
    val showFeedback: (String) -> Unit = { message ->
        feedbackJob?.cancel()
        feedbackJob = coroutineScope.launch {
            withTimeoutOrNull(2_000L) {
                snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Indefinite)
            }
        }
    }

    val selectedSubscription = SubscriptionStore.findById(selectedSubscriptionId)

    // If the subscription no longer exists, return to the list instead of showing an empty screen.
    val requestedDestination = SubybaraDestination.fromRoute(currentRoute)
    val needsSelectedSubscription =
        requestedDestination == SubybaraDestination.SubscriptionDetails ||
            requestedDestination == SubybaraDestination.EditSubscription
    val destination = if (needsSelectedSubscription && selectedSubscription == null) {
        SubybaraDestination.Subscriptions
    } else {
        requestedDestination
    }

    // Let forms handle Back first if there are unsaved changes.
    BackHandler(
        enabled = destination == SubybaraDestination.SubscriptionDetails ||
            destination == SubybaraDestination.AddSubscription ||
            destination == SubybaraDestination.EditSubscription
    ) {
        navigator.navigateTo(
            when (destination) {
                SubybaraDestination.EditSubscription -> SubybaraDestination.SubscriptionDetails
                SubybaraDestination.AddSubscription ->
                    SubybaraDestination.fromRoute(addSubscriptionOrigin)

                else -> SubybaraDestination.Subscriptions
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (destination) {
            SubybaraDestination.Home -> HomeScreen(
                onDestinationClick = onMainDestinationClick,
                onAddSubscriptionClick = onAddSubscriptionClick
            )

            SubybaraDestination.Subscriptions -> SubscriptionsScreen(
                subscriptions = SubscriptionStore.subscriptions,
                onSubscriptionClick = { subscription ->
                    selectedSubscriptionId = subscription.id
                    navigator.navigateTo(SubybaraDestination.SubscriptionDetails)
                },
                onDestinationClick = onMainDestinationClick,
                onAddSubscriptionClick = onAddSubscriptionClick
            )

            SubybaraDestination.SubscriptionDetails -> selectedSubscription?.let { subscription ->
                SubscriptionDetailsScreen(
                    subscription = subscription,
                    onBackClick = { navigator.navigateTo(SubybaraDestination.Subscriptions) },
                    onEditClick = { navigator.navigateTo(SubybaraDestination.EditSubscription) },
                    onStatusChange = { status ->
                        if (SubscriptionStore.changeStatus(subscription.id, status)) {
                            showFeedback("Tracking status updated")
                        }
                    },
                    onRemoveSubscriptionClick = {
                        SubscriptionStore.remove(subscription.id)
                        navigator.navigateTo(SubybaraDestination.Subscriptions)
                        showFeedback("Subscription deleted")
                    }
                )
            }

            SubybaraDestination.AddSubscription -> AddSubscriptionScreen(
                onCloseClick = {
                    navigator.navigateTo(SubybaraDestination.fromRoute(addSubscriptionOrigin))
                },
                onSaveClick = { subscription -> SubscriptionStore.add(subscription) },
                onDoneClick = { navigator.navigateTo(SubybaraDestination.Subscriptions) }
            )

            SubybaraDestination.EditSubscription -> selectedSubscription?.let { subscription ->
                EditSubscriptionScreen(
                    subscription = subscription,
                    onCloseClick = {
                        navigator.navigateTo(SubybaraDestination.SubscriptionDetails)
                    },
                    onSaveClick = { updatedSubscription ->
                        SubscriptionStore.update(updatedSubscription)
                        navigator.navigateTo(SubybaraDestination.SubscriptionDetails)
                        showFeedback("Changes saved!")
                    }
                )
            }

            else -> WelcomeScreen(
                onGetStartedClick = { navigator.navigateTo(SubybaraDestination.Home) },
                onSignInClick = { navigator.navigateTo(SubybaraDestination.SignIn) }
            )
        }

        // Feedback pills float over every screen, just below the status bar.
        SubscriptionSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )
    }
}
