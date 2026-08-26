package com.moufee.purduemenus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colors mirror the previous values/colors.xml and values-night/colors.xml palettes
private val LightColors = lightColors(
    primary = Color(0xFFFFC107),
    primaryVariant = Color(0xFFFFA000),
    secondary = Color(0xFF455A64),
    secondaryVariant = Color(0xFF1C313A),
    onPrimary = Color.Black,
)

private val DarkColors = darkColors(
    primary = Color(0xFF424242),
    primaryVariant = Color(0xFF212121),
    secondary = Color(0xFFFFC107),
    onPrimary = Color.White,
)

// headerColor resolves to #ffa000 in both light and night resource sets
val StationHeaderColor = Color(0xFFFFA000)

@Composable
fun MenusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
