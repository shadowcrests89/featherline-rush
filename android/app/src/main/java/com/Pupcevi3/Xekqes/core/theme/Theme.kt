package com.Pupcevi3.Xekqes.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val FeatherlineScheme = darkColorScheme(
    primary = ScarletFeather,
    onPrimary = TextPrimary,
    primaryContainer = EmberTail,
    onPrimaryContainer = TextPrimary,
    secondary = LaneAmber,
    onSecondary = AsphaltBlack,
    secondaryContainer = SurfaceRaised,
    onSecondaryContainer = LaneAmber,
    tertiary = RainStreak,
    onTertiary = TextPrimary,
    background = AsphaltBlack,
    onBackground = TextPrimary,
    surface = SurfaceNight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceRaised,
    onSurfaceVariant = TextSecondary,
    outline = TextMuted,
    outlineVariant = DisabledBorder,
    error = ScarletFeather,
    onError = TextPrimary,
    scrim = AsphaltBlack
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FeatherlineScheme,
        typography = AppTypography,
        content = content
    )
}
