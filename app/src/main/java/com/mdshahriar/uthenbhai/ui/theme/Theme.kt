package com.mdshahriar.uthenbhai.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.material3.ColorScheme

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFBA7A),
    onPrimary = Color(0xFF482400),
    primaryContainer = Color(0xFF49301F),
    onPrimaryContainer = Color(0xFFFFDCC0),
    secondary = OrangeLight,
    onSecondary = Color(0xFF482400),
    tertiary = OrangeLight,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkCard,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = WhitePure,
    primaryContainer = OrangeSoft,
    onPrimaryContainer = OrangeDeep,
    secondary = OrangeVibrant,
    onSecondary = WhitePure,
    tertiary = OrangeAccent,
    background = WhiteSurface,
    onBackground = GrayTextPrimary,
    surface = WhitePure,
    onSurface = GrayTextPrimary,
    surfaceVariant = WhiteSurface,
    onSurfaceVariant = GrayTextSecondary,
    outline = WhiteBorder
)

@Composable
fun UthenBhaiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/** Success text stays readable on both the light and dark surfaces. */
val ColorScheme.success: Color
    get() = if (surface.luminance() < 0.5f) Color(0xFF75D9B7) else CashGreen
