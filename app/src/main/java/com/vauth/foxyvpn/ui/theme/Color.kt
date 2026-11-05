package com.vauth.foxyvpn.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val md_theme_light_primary = Color(0x0B3D91)
val md_theme_light_onPrimary = Color(0xFFFFFF)
val md_theme_light_primaryContainer = Color(0xD6E4FF)
val md_theme_light_onPrimaryContainer = Color(0x001A41)
val md_theme_light_inversePrimary = Color(0x8FB3FF)

val md_theme_light_secondary = Color(0xB07D12)
val md_theme_light_onSecondary = Color(0xFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFEFC2)
val md_theme_light_onSecondaryContainer = Color(0x3D2800)

val md_theme_light_tertiary = Color(0xFF64612E)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFEBE7A6)
val md_theme_light_onTertiaryContainer = Color(0xFF1E1D00)

val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onErrorContainer = Color(0xFF410002)

val md_theme_light_background = Color(0xF3F6FC)
val md_theme_light_onBackground = Color(0x0A1B3D)
val md_theme_light_surface = Color(0xF3F6FC)
val md_theme_light_onSurface = Color(0x0A1B3D)
val md_theme_light_surfaceVariant = Color(0xDCE5F5)
val md_theme_light_onSurfaceVariant = Color(0x3A4D70)
val md_theme_light_surfaceTint = md_theme_light_primary

val md_theme_light_surfaceContainerLowest = Color(0xFFFFFFFF)
val md_theme_light_surfaceContainerLow = Color(0xEBF1FB)
val md_theme_light_surfaceContainer = Color(0xE3EBF8)
val md_theme_light_surfaceContainerHigh = Color(0xFFF7E4DE)
val md_theme_light_surfaceContainerHighest = Color(0xFFF1DED8)
val md_theme_light_surfaceBright = Color(0xFFFFF8F6)
val md_theme_light_surfaceDim = Color(0xFFE6D5CF)

val md_theme_light_outline = Color(0xFF85736C)
val md_theme_light_outlineVariant = Color(0xFFD8C2BA)
val md_theme_light_inverseSurface = Color(0xFF382E2A)
val md_theme_light_inverseOnSurface = Color(0xFFFFEDE7)
val md_theme_light_scrim = Color(0xFF000000)

val md_theme_dark_primary = Color(0x7FB0FF)
val md_theme_dark_onPrimary = Color(0x002C6B)
val md_theme_dark_primaryContainer = Color(0x0B3D91)
val md_theme_dark_onPrimaryContainer = Color(0xD6E4FF)
val md_theme_dark_inversePrimary = Color(0x0B3D91)

val md_theme_dark_secondary = Color(0xF0C35A)
val md_theme_dark_onSecondary = Color(0x3D2800)
val md_theme_dark_secondaryContainer = Color(0xFF5C4035)
val md_theme_dark_onSecondaryContainer = Color(0xFFFFDBCB)

val md_theme_dark_tertiary = Color(0xFFCFCB8D)
val md_theme_dark_onTertiary = Color(0xFF343205)
val md_theme_dark_tertiaryContainer = Color(0xFF4B4919)
val md_theme_dark_onTertiaryContainer = Color(0xFFEBE7A6)

val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)

val md_theme_dark_background = Color(0x061633)
val md_theme_dark_onBackground = Color(0xE1EAFB)
val md_theme_dark_surface = Color(0x061633)
val md_theme_dark_onSurface = Color(0xE1EAFB)
val md_theme_dark_surfaceVariant = Color(0xFF53433D)
val md_theme_dark_onSurfaceVariant = Color(0xFFD8C2BA)
val md_theme_dark_surfaceTint = md_theme_dark_primary

val md_theme_dark_surfaceContainerLowest = Color(0xFF140C0A)
val md_theme_dark_surfaceContainerLow = Color(0x0C2250)
val md_theme_dark_surfaceContainer = Color(0x112A60)
val md_theme_dark_surfaceContainerHigh = Color(0xFF322824)
val md_theme_dark_surfaceContainerHighest = Color(0xFF3D332E)
val md_theme_dark_surfaceBright = Color(0xFF413733)
val md_theme_dark_surfaceDim = Color(0xFF1A1210)

val md_theme_dark_outline = Color(0xFFA08D85)
val md_theme_dark_outlineVariant = Color(0xFF53433D)
val md_theme_dark_inverseSurface = Color(0xFFF1DED8)
val md_theme_dark_inverseOnSurface = Color(0xFF382E2A)
val md_theme_dark_scrim = Color(0xFF000000)

data class FoxyStatusColors(val connected: Color, val connecting: Color)

val LightStatusColors =
    FoxyStatusColors(connected = Color(0xFF1B7A3A), connecting = Color(0xFFB07D12))

val DarkStatusColors =
    FoxyStatusColors(connected = Color(0xFF7BDF92), connecting = Color(0xFFFFC46B))

val LocalFoxyStatusColors = staticCompositionLocalOf { LightStatusColors }
