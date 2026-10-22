package com.example.actesimple.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = CivicBluePrimary,
    onPrimary = LightSurface,
    primaryContainer = CivicBlueContainer,
    onPrimaryContainer = CivicBlueOnContainer,
    secondary = CivicTealSecondary,
    onSecondary = LightSurface,
    secondaryContainer = CivicTealContainer,
    onSecondaryContainer = CivicTealOnContainer,
    tertiary = CivicGoldTertiary,
    onTertiary = LightSurface,
    tertiaryContainer = CivicGoldContainer,
    onTertiaryContainer = CivicGoldOnContainer,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = CivicBlueContainer,
    onPrimary = DarkBackground,
    primaryContainer = CivicBluePrimary,
    onPrimaryContainer = LightSurface,
    secondary = CivicTealContainer,
    onSecondary = DarkBackground,
    secondaryContainer = CivicTealSecondary,
    onSecondaryContainer = LightSurface,
    tertiary = CivicGoldContainer,
    onTertiary = DarkBackground,
    tertiaryContainer = CivicGoldTertiary,
    onTertiaryContainer = LightSurface,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline
)

@Composable
fun ActeSimpleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
