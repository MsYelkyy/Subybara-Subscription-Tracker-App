package com.subybara.app.navigation

import androidx.compose.runtime.Composable
import com.subybara.app.feature.welcome.presentation.WelcomeRoute

@Composable
fun SubybaraNavHost(
    navigator: DestinationNavigator = DestinationNavigator {}
) {
    WelcomeRoute(
        onGetStarted = { navigator.navigateTo(SubybaraDestination.Home) },
        onSignIn = { navigator.navigateTo(SubybaraDestination.SignIn) }
    )
}
