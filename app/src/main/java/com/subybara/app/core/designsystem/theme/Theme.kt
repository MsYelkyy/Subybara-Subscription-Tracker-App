package com.subybara.app.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SubybaraLightGreen,
    secondary = SubybaraSoftBrown,
    tertiary = SubybaraGreen,
    background = SubybaraDarkGreen,
    surface = SubybaraDarkGreen,
    onPrimary = SubybaraDarkGreen,
    onSecondary = SubybaraDarkGreen,
    onTertiary = SubybaraCream,
    onBackground = SubybaraCream,
    onSurface = SubybaraCream
)

private val LightColorScheme = lightColorScheme(
    primary = SubybaraGreen,
    secondary = SubybaraBrown,
    tertiary = SubybaraSoftBrown,
    background = SubybaraCream,
    surface = SubybaraCream,
    onPrimary = SubybaraCream,
    onSecondary = SubybaraCream,
    onTertiary = SubybaraDarkGreen,
    onBackground = SubybaraBrown,
    onSurface = SubybaraBrown
)

@Composable
fun SubybaraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
