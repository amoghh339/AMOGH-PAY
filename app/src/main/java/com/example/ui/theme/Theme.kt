package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = FamYellow,
    onPrimary = Color(0xFF1A1300),
    primaryContainer = Color(0xFF382900),
    onPrimaryContainer = FamYellowLight,
    secondary = FamCyan,
    onSecondary = Color(0xFF002022),
    secondaryContainer = Color(0xFF004F54),
    onSecondaryContainer = Color(0xFF97F0FF),
    tertiary = FamPurple,
    onTertiary = Color.White,
    background = FamDarkBackground,
    onBackground = FamTextPrimary,
    surface = FamDarkSurface,
    onSurface = FamTextPrimary,
    surfaceVariant = FamDarkSurfaceElevated,
    onSurfaceVariant = FamTextSecondary,
    outline = FamDarkBorder,
    error = FamRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // FamPay is iconic for its dark fintech UI
    dynamicColor: Boolean = false, // Keep brand aesthetic
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
