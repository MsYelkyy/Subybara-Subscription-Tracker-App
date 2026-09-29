package com.subybara.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.feature.account.WelcomeScreen
import com.subybara.app.feature.home.HomeScreen
import com.subybara.app.feature.subscriptions.SubscriptionsScreen

@Composable
fun SubybaraNavHost() {
    var currentRoute by rememberSaveable { mutableStateOf(SubybaraDestination.Welcome.route) }
    val navigator = DestinationNavigator { destination -> currentRoute = destination.route }

    val onMainDestinationClick: (SubybaraDestination) -> Unit = { destination ->
        if (destination == SubybaraDestination.Home ||
            destination == SubybaraDestination.Subscriptions
        ) {
            navigator.navigateTo(destination)
        }
    }

    when (SubybaraDestination.fromRoute(currentRoute)) {
        SubybaraDestination.Home -> HomeScreen(
            onDestinationClick = onMainDestinationClick,
            onAddSubscriptionClick = {}
        )

        SubybaraDestination.Subscriptions -> SubscriptionsScreen(
            subscriptions = SubscriptionStore.subscriptions,
            onDestinationClick = onMainDestinationClick,
            onAddSubscriptionClick = {}
        )

        else -> WelcomeScreen(
            onGetStartedClick = { navigator.navigateTo(SubybaraDestination.Home) },
            onSignInClick = { navigator.navigateTo(SubybaraDestination.SignIn) }
        )
    }
}
