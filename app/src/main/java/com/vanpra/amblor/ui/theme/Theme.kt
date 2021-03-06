package com.vanpra.amblor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets

private val DarkColorPalette = darkColors(
    primary = blue500,
    primaryVariant = blue700,
    secondary = blue200,
    onSurface = Color.White,
    onBackground = Color.White,
    onPrimary = Color.White,
    background = darkBackground
)

private val LightColorPalette = lightColors(
    primary = blue500,
    primaryVariant = blue700,
    onPrimary = Color.White,
    secondary = blue200
)

@Composable
fun AmblorTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
    ) {
        ProvideWindowInsets {
            content()
        }
    }
}
