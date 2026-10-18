package com.subybara.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.subybara.app.core.data.SubscriptionStore
import com.subybara.app.feature.account.ForgotPasswordScreen
import com.subybara.app.feature.account.SignInScreen
import com.subybara.app.feature.account.SignUpScreen
import com.subybara.app.feature.account.WelcomeScreen
import com.subybara.app.feature.home.HomeScreen
import com.subybara.app.feature.profile.ProfileScreen
import com.subybara.app.feature.subscriptions.SubscriptionsScreen

@Composable
fun SubybaraNavHost() {
    var currentRoute by rememberSaveable { mutableStateOf(SubybaraDestination.Welcome.route) }
    val navigator = DestinationNavigator { destination -> currentRoute = destination.route }

    val onMainDestinationClick: (SubybaraDestination) -> Unit = { destination ->
        if (destination == SubybaraDestination.Home ||
            destination == SubybaraDestination.Subscriptions ||
            destination == SubybaraDestination.Profile
        ) {
            navigator.navigateTo(destination)
        }
    }

    when (SubybaraDestination.fromRoute(currentRoute)) {
        SubybaraDestination.Welcome -> WelcomeScreen(
            onGetStartedClick = { navigator.navigateTo(SubybaraDestination.Home) },
            onSignInClick = { navigator.navigateTo(SubybaraDestination.SignIn) }
        )

        SubybaraDestination.SignIn -> SignInScreen(
            onSignInSuccess = { navigator.navigateTo(SubybaraDestination.Home) },
            onForgotPasswordClick = { navigator.navigateTo(SubybaraDestination.ForgotPassword) },
            onSignUpClick = { navigator.navigateTo(SubybaraDestination.SignUp) },
            onBackClick = { navigator.navigateTo(SubybaraDestination.Welcome) }
        )

        SubybaraDestination.SignUp -> SignUpScreen(
            onSignUpSuccess = { navigator.navigateTo(SubybaraDestination.SignIn) },
            onSignInClick = { navigator.navigateTo(SubybaraDestination.SignIn) }
        )

        SubybaraDestination.ForgotPassword -> ForgotPasswordScreen(
            onBackToSignIn = { navigator.navigateTo(SubybaraDestination.SignIn) }
        )

        SubybaraDestination.Home -> HomeScreen(
            onDestinationClick = onMainDestinationClick,
            onAddSubscriptionClick = {}
        )

        SubybaraDestination.Subscriptions -> SubscriptionsScreen(
            subscriptions = SubscriptionStore.subscriptions,
            onDestinationClick = onMainDestinationClick,
            onAddSubscriptionClick = {}
        )

        SubybaraDestination.Profile -> ProfileScreen(
            onDestinationClick = onMainDestinationClick,
            onAddSubscriptionClick = {},
            onSignOutClick = { navigator.navigateTo(SubybaraDestination.Welcome) }
        )

        else -> WelcomeScreen(
            onGetStartedClick = { navigator.navigateTo(SubybaraDestination.Home) },
            onSignInClick = { navigator.navigateTo(SubybaraDestination.SignIn) }
        )
    }
}
