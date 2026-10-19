package com.subybara.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.feature.account.WelcomeScreen
import com.subybara.app.feature.home.HomeScreen
import com.subybara.app.feature.insights.InsightsScreen
import com.subybara.app.feature.renewals.UpcomingRenewalsScreen
import com.subybara.app.feature.subscriptions.SubscriptionsScreen

@Composable
fun SubybaraNavHost() {
    var currentRoute by rememberSaveable { mutableStateOf(SubybaraDestination.Welcome.route) }
    val navigator = DestinationNavigator { destination -> currentRoute = destination.route }

    val onMainDestinationClick: (SubybaraDestination) -> Unit = { destination ->
        navigator.navigateTo(destination)
    }

    when (SubybaraDestination.fromRoute(currentRoute)) {
        SubybaraDestination.Home -> HomeScreen(
            subscriptions = SubscriptionStore.subscriptions,
            onDestinationClick = onMainDestinationClick,
            onAddSubscriptionClick = {},
            onViewAllRenewalsClick = { navigator.navigateTo(SubybaraDestination.Renewals) },
            onSelectSubscription = { // Navigate to detail screen when ready
            }
        )

        SubybaraDestination.Subscriptions -> SubscriptionsScreen(
            subscriptions = SubscriptionStore.subscriptions,
            onDestinationClick = onMainDestinationClick,
            onAddSubscriptionClick = {}
        )

        SubybaraDestination.Renewals -> UpcomingRenewalsScreen(
            subscriptions = SubscriptionStore.subscriptions,
            onNavigateBack = { navigator.navigateTo(SubybaraDestination.Home) }
        )

        SubybaraDestination.Insights -> InsightsScreen(
            subscriptions = SubscriptionStore.subscriptions,
            onDestinationClick = onMainDestinationClick
        )

        else -> WelcomeScreen(
            onGetStartedClick = { navigator.navigateTo(SubybaraDestination.Home) },
            onSignInClick = { navigator.navigateTo(SubybaraDestination.SignIn) }
        )
    }
}