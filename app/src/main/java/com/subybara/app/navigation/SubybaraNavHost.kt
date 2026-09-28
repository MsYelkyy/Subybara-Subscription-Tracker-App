package com.subybara.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.subybara.app.feature.account.WelcomeScreen
import com.subybara.app.feature.home.HomeScreen

@Composable
fun SubybaraNavHost() {
    var currentRoute by rememberSaveable { mutableStateOf(SubybaraDestination.Welcome.route) }
    val navigator = DestinationNavigator { destination -> currentRoute = destination.route }

    when (SubybaraDestination.fromRoute(currentRoute)) {
        SubybaraDestination.Home -> HomeScreen(
            onDestinationClick = {},
            onAddSubscriptionClick = {}
        )

        else -> WelcomeScreen(
            onGetStartedClick = { navigator.navigateTo(SubybaraDestination.Home) },
            onSignInClick = { navigator.navigateTo(SubybaraDestination.SignIn) }
        )
    }
}
