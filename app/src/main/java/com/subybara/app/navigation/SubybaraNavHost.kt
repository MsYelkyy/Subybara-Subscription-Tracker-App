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
import com.subybara.app.core.model.Subscription
import com.subybara.app.feature.account.ForgotPasswordScreen
import com.subybara.app.feature.account.SignInScreen
import com.subybara.app.feature.account.SignUpScreen
import com.subybara.app.feature.account.WelcomeScreen
import com.subybara.app.feature.home.HomeScreen
import com.subybara.app.feature.insights.InsightsScreen
import com.subybara.app.feature.notifications.NotificationsScreen
import com.subybara.app.feature.profile.ProfileScreen
import com.subybara.app.feature.renewals.UpcomingRenewalsScreen
import com.subybara.app.feature.subscriptions.AddSubscriptionScreen
import com.subybara.app.feature.subscriptions.EditSubscriptionScreen
import com.subybara.app.feature.subscriptions.SubscriptionDetailsScreen
import com.subybara.app.feature.subscriptions.SubscriptionSnackbarHost
import com.subybara.app.feature.subscriptions.SubscriptionsScreen
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

@Composable
fun SubybaraNavHost() {
    var currentRoute by rememberSaveable { mutableStateOf(SubybaraDestination.Welcome.route) }
    var selectedSubscriptionId by rememberSaveable { mutableIntStateOf(0) }
    var addSubscriptionOrigin by rememberSaveable {
        mutableStateOf(SubybaraDestination.Subscriptions.route)
    }
    var subscriptionDetailsOrigin by rememberSaveable {
        mutableStateOf(SubybaraDestination.Subscriptions.route)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val navigator = DestinationNavigator { destination -> currentRoute = destination.route }

    val onMainDestinationClick: (SubybaraDestination) -> Unit = { destination ->
        if (destination == SubybaraDestination.Home ||
            destination == SubybaraDestination.Subscriptions ||
            destination == SubybaraDestination.Insights ||
            destination == SubybaraDestination.Profile
        ) {
            navigator.navigateTo(destination)
        }
    }

    // Remember where the add button was tapped so closing the form returns there.
    val onAddSubscriptionClick: () -> Unit = {
        addSubscriptionOrigin = currentRoute
        navigator.navigateTo(SubybaraDestination.AddSubscription)
    }

    val onSubscriptionClick: (Subscription) -> Unit = { subscription ->
        selectedSubscriptionId = subscription.id
        subscriptionDetailsOrigin = currentRoute
        navigator.navigateTo(SubybaraDestination.SubscriptionDetails)
    }

    // Replace the current message so repeated taps don't queue old snackbars.
    var feedbackJob by remember { mutableStateOf<Job?>(null) }
    val showFeedback: (String) -> Unit = { message ->
        feedbackJob?.cancel()
        feedbackJob = coroutineScope.launch {
            withTimeoutOrNull(2.seconds) {
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
        enabled = destination == SubybaraDestination.SignIn ||
            destination == SubybaraDestination.SignUp ||
            destination == SubybaraDestination.Subscriptions ||
            destination == SubybaraDestination.Insights ||
            destination == SubybaraDestination.Profile ||
            destination == SubybaraDestination.SubscriptionDetails ||
            destination == SubybaraDestination.AddSubscription ||
            destination == SubybaraDestination.EditSubscription ||
            destination == SubybaraDestination.Renewals ||
            destination == SubybaraDestination.Notifications
    ) {
        navigator.navigateTo(
            when (destination) {
                SubybaraDestination.SignIn -> SubybaraDestination.Welcome
                SubybaraDestination.SignUp -> SubybaraDestination.SignIn
                SubybaraDestination.EditSubscription -> SubybaraDestination.SubscriptionDetails
                SubybaraDestination.SubscriptionDetails ->
                    SubybaraDestination.fromRoute(subscriptionDetailsOrigin)

                SubybaraDestination.AddSubscription ->
                    SubybaraDestination.fromRoute(addSubscriptionOrigin)

                SubybaraDestination.Subscriptions,
                SubybaraDestination.Insights,
                SubybaraDestination.Profile,
                SubybaraDestination.Renewals,
                SubybaraDestination.Notifications -> SubybaraDestination.Home

                else -> SubybaraDestination.Subscriptions
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (destination) {
            SubybaraDestination.Welcome -> WelcomeScreen(
                onGetStartedClick = { navigator.navigateTo(SubybaraDestination.Home) },
                onSignInClick = { navigator.navigateTo(SubybaraDestination.SignIn) }
            )

            SubybaraDestination.SignIn -> SignInScreen(
                onSignInSuccess = { navigator.navigateTo(SubybaraDestination.Home) },
                onForgotPasswordClick = { navigator.navigateTo(SubybaraDestination.ForgotPassword) },
                onSignUpClick = { navigator.navigateTo(SubybaraDestination.SignUp) }
            )

            SubybaraDestination.SignUp -> SignUpScreen(
                onSignUpSuccess = { navigator.navigateTo(SubybaraDestination.SignIn) },
                onSignInClick = { navigator.navigateTo(SubybaraDestination.SignIn) }
            )

            SubybaraDestination.ForgotPassword -> ForgotPasswordScreen(
                onBackToSignIn = { navigator.navigateTo(SubybaraDestination.SignIn) }
            )

            SubybaraDestination.Home -> HomeScreen(
                subscriptions = SubscriptionStore.subscriptions,
                onDestinationClick = onMainDestinationClick,
                onAddSubscriptionClick = onAddSubscriptionClick,
                onViewAllRenewalsClick = { navigator.navigateTo(SubybaraDestination.Renewals) },
                onSelectSubscription = onSubscriptionClick,
                onNotificationsClick = { navigator.navigateTo(SubybaraDestination.Notifications) }
            )

            SubybaraDestination.Subscriptions -> SubscriptionsScreen(
                subscriptions = SubscriptionStore.subscriptions,
                onSubscriptionClick = onSubscriptionClick,
                onDestinationClick = onMainDestinationClick,
                onAddSubscriptionClick = onAddSubscriptionClick
            )

            SubybaraDestination.SubscriptionDetails -> selectedSubscription?.let { subscription ->
                SubscriptionDetailsScreen(
                    subscription = subscription,
                    onBackClick = {
                        navigator.navigateTo(SubybaraDestination.fromRoute(subscriptionDetailsOrigin))
                    },
                    onEditClick = { navigator.navigateTo(SubybaraDestination.EditSubscription) },
                    onStatusChange = { status ->
                        if (SubscriptionStore.changeStatus(subscription.id, status)) {
                            showFeedback("Tracking status updated")
                        }
                    },
                    onRemoveSubscriptionClick = {
                        SubscriptionStore.remove(subscription.id)
                        navigator.navigateTo(SubybaraDestination.fromRoute(subscriptionDetailsOrigin))
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

            SubybaraDestination.Profile -> ProfileScreen(
                onDestinationClick = onMainDestinationClick,
                onAddSubscriptionClick = onAddSubscriptionClick,
                onSignOutClick = { navigator.navigateTo(SubybaraDestination.Welcome) }
            )

            SubybaraDestination.Renewals -> UpcomingRenewalsScreen(
                subscriptions = SubscriptionStore.subscriptions,
                onNavigateBack = { navigator.navigateTo(SubybaraDestination.Home) },
                onSelectSubscription = onSubscriptionClick
            )

            SubybaraDestination.Notifications -> NotificationsScreen(
                onBackClick = { navigator.navigateTo(SubybaraDestination.Home) }
            )

            SubybaraDestination.Insights -> InsightsScreen(
                subscriptions = SubscriptionStore.subscriptions,
                onDestinationClick = onMainDestinationClick,
                onAddSubscriptionClick = onAddSubscriptionClick
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