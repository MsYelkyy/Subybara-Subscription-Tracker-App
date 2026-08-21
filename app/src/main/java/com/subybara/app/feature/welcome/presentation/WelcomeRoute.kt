package com.subybara.app.feature.welcome.presentation

import androidx.compose.runtime.Composable

@Composable
fun WelcomeRoute(
    onGetStarted: () -> Unit,
    onSignIn: () -> Unit
) {
    WelcomeScreen(
        state = WelcomeUiState(),
        onAction = { action ->
            when (action) {
                WelcomeAction.GetStarted -> onGetStarted()
                WelcomeAction.SignIn -> onSignIn()
            }
        }
    )
}
