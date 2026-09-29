package com.subybara.app.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    // Main brand color
    primary = Primary600,
    onPrimary = White,

    // Supporting green
    secondary = Primary300,
    onSecondary = Neutral800,

    // Accent yellow
    tertiary = Tertiary500,
    onTertiary = Neutral900,

    // Main screen background
    background = Tertiary100,
    onBackground = Neutral800,

    // Cards, fields, and other surfaces
    surface = White,
    onSurface = Neutral800,
    onSurfaceVariant = Neutral500,

    // Borders and dividers
    outline = Neutral300
)

@Composable
fun SubybaraTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
