package com.subybara.app.feature.welcome.presentation

data class WelcomeUiState(
    val title: String = "Subybara",
    val headline: String = "Track your subscriptions.\nAvoid surprise charges.",
    val primaryActionLabel: String = "Get Started",
    val secondaryActionLabel: String = "I already have an account"
)

sealed interface WelcomeAction {
    data object GetStarted : WelcomeAction
    data object SignIn : WelcomeAction
}
