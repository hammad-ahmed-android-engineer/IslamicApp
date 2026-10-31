package com.ne.muslimprayers.qiblafinder.core.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Dedicated Dark Theme ColorScheme
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreenDark,
    onPrimary = DarkBackground,
    primaryContainer = DarkElevatedSurface,
    onPrimaryContainer = PrimaryTextDark,
    secondary = GoldAccentDark,
    onSecondary = DarkBackground,
    secondaryContainer = DarkElevatedCard,
    onSecondaryContainer = DesertOchre,
    tertiary = SecondaryGreenDark,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = PrimaryTextDark,
    surface = DarkSurface,
    onSurface = PrimaryTextDark,
    surfaceVariant = DarkElevatedSurface,
    onSurfaceVariant = SecondaryTextDark,
    outline = DividerDark,
    outlineVariant = DividerDark
)

// Dedicated Light Theme ColorScheme (Alabaster Parchment & Warm Sage)
private val LightColorScheme = lightColorScheme(
    primary = MedinaSage,
    onPrimary = PureAlabaster,
    primaryContainer = MedinaSageLight,
    onPrimaryContainer = MedinaSageDark,
    secondary = DesertOchre,
    onSecondary = PureAlabaster,
    secondaryContainer = DesertOchreLight,
    onSecondaryContainer = DesertOchre,
    tertiary = MedinaSage,
    onTertiary = PureAlabaster,
    background = WarmParchment,
    onBackground = TextPrimary,
    surface = PureAlabaster,
    onSurface = TextPrimary,
    surfaceVariant = MedinaSageLight,
    onSurfaceVariant = TextSecondary,
    outline = DividerColor,
    outlineVariant = DividerColor
)

@Composable
fun IslamicAppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography.toMaterial3Typography(),
        shapes = Shapes,
        content = content
    )
}
