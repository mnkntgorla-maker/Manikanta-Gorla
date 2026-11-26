package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val PomanaDarkColorScheme = darkColorScheme(
    primary = PomanaEmeraldBright,
    onPrimary = Color(0xFF03210E),
    primaryContainer = Color(0xFF114628),
    onPrimaryContainer = PomanaMintContainer,
    secondary = PomanaAmber,
    onSecondary = Color(0xFF281800),
    secondaryContainer = Color(0xFF452B00),
    onSecondaryContainer = PomanaAmberContainer,
    tertiary = PomanaSky,
    onTertiary = Color(0xFF001E2E),
    tertiaryContainer = Color(0xFF0B3752),
    onTertiaryContainer = PomanaSkyContainer,
    error = PomanaCoral,
    onError = Color.White,
    errorContainer = Color(0xFF4C0F1D),
    onErrorContainer = PomanaCoralContainer,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceMuted,
    outline = DarkOutline,
    outlineVariant = Color(0xFF22362C)
)

private val PomanaLightColorScheme = lightColorScheme(
    primary = PomanaEmerald,
    onPrimary = Color.White,
    primaryContainer = PomanaMintContainer,
    onPrimaryContainer = Color(0xFF064E27),
    secondary = PomanaAmber,
    onSecondary = Color.White,
    secondaryContainer = PomanaAmberContainer,
    onSecondaryContainer = Color(0xFF6B3E00),
    tertiary = PomanaSky,
    onTertiary = Color.White,
    tertiaryContainer = PomanaSkyContainer,
    onTertiaryContainer = Color(0xFF044061),
    error = PomanaCoral,
    onError = Color.White,
    errorContainer = PomanaCoralContainer,
    onErrorContainer = Color(0xFF7A0D25),
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceMuted,
    outline = LightOutline,
    outlineVariant = Color(0xFFE0EDE4)
)

val PomanaShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

@Composable
fun PomanaClubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) PomanaDarkColorScheme else PomanaLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PomanaTypography,
        shapes = PomanaShapes,
        content = content
    )
}
