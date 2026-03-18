package com.robusthealth.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Fuchsia,
    onPrimary = Color.Black,
    secondary = Indigo,
    onSecondary = Color.White,
    tertiary = Emerald,
    background = Slate50,
    surface = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val DarkColors = darkColorScheme(
    primary = Fuchsia,
    onPrimary = Color.Black,
    secondary = Indigo,
    onSecondary = Color.White,
    tertiary = Emerald,
    background = Slate950,
    surface = Slate900,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun RobustHealthTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) {
        DarkColors.copy(
            primaryContainer = Fuchsia.copy(alpha = 0.16f),
            onPrimaryContainer = Color.White,
            secondaryContainer = Indigo.copy(alpha = 0.18f),
            tertiaryContainer = Emerald.copy(alpha = 0.18f)
        )
    } else {
        LightColors.copy(
            primaryContainer = Fuchsia.copy(alpha = 0.12f),
            onPrimaryContainer = Slate950,
            secondaryContainer = Indigo.copy(alpha = 0.12f),
            tertiaryContainer = Emerald.copy(alpha = 0.12f)
        )
    }
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
