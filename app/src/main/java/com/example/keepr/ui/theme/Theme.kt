package com.example.keepr.ui.theme

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

private val KeeprLightColorScheme = lightColorScheme(
    primary = KeeprBrand,
    onPrimary = KeeprDark,
    surface = KeeprOffWhite,
    onSurface = KeeprAccent,
    background = KeeprLightGreenBg,
    onBackground = KeeprAccent,
    outline = KeeprAccent.copy(alpha = 0.5f),
    error = KeeprErrorRed,
    onError = KeeprWhite
)

private val KeeprDarkColorScheme = darkColorScheme(
    primary = KeeprBrand,
    onPrimary = KeeprDark,
    surface = KeeprAccent.copy(alpha = 0.3f),
    onSurface = KeeprWhite,
    background = KeeprDark,
    onBackground = KeeprWhite,
    outline = KeeprBrand.copy(alpha = 0.4f),
    error = KeeprErrorRed,
    onError = KeeprWhite
)

@Composable
fun KeeprTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> KeeprDarkColorScheme
        else -> KeeprLightColorScheme
    }

    // Kode for å fargelegge system-navbaren
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
