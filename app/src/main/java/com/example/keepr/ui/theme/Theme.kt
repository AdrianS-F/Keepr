package com.example.keepr.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Dark scheme (bruker de mørke grønntonene som bakgrunn/surface)

private val KeeprDarkColorScheme = darkColorScheme(
    background = KeeprDark,
    onBackground = Color.White,
    // Ting du trykker på (knapper, primært)
    primary = KeeprTeal,
    onPrimary = Color.White,
    // Flater/kort/inputs – BRUK denne i tekstfelt
    surface = KeeprDark,                 // hovedflate (screen)
    onSurface = Color.White,
    surfaceVariant = KeeprMedium,        // <— LYSERE grønn for kort/inputs
    // Kantfarge for OutlinedTextField m.m.
    outline = KeeprLight.copy(alpha = 0.60f),
    // valgfritt: aksenter
    secondary = KeeprMedium,
    onSecondary = Color.White
)

// Light scheme (lys bakgrunn, brand-farger på primary/secondary)
private val KeeprLightColorScheme = lightColorScheme(
    primary = KeeprTeal,          // #1A4A47
    onPrimary = Color.White,
    secondary = KeeprMedium,      // #537D79
    onSecondary = Color.White,
    tertiary = KeeprLight,        // #B5E0BD
    onTertiary = KeeprDark,
    background = Color.White,
    onBackground = KeeprDark,     // #1C393B
    surface = Color.White,
    onSurface = KeeprDark
)

@Composable
fun KeeprTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Sett til false for å sikre at brand-paletten alltid brukes
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
