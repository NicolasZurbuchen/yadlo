package io.nicolaszurbuchen.yadlo.app.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

// Material's scheme is derived from the same palette rather than being a second source of colour.
// Anything Material draws for us — a NavigationBar, a Button, a ripple — has to land on the
// identity without every call site overriding it.
private val LightMaterialColorScheme =
    lightColorScheme(
        primary = YadloPalette.primaryLight,
        onPrimary = YadloPalette.backgroundLight,
        primaryContainer = YadloPalette.brandLight,
        onPrimaryContainer = YadloPalette.onBrandLight,
        secondary = YadloPalette.accent,
        onSecondary = YadloPalette.inkLight,
        background = YadloPalette.backgroundLight,
        onBackground = YadloPalette.inkLight,
        surface = YadloPalette.backgroundLight,
        onSurface = YadloPalette.inkLight,
        surfaceVariant = YadloPalette.surfaceLight,
        onSurfaceVariant = YadloPalette.inkMutedLight,
        outline = YadloPalette.inkSoftLight,
        outlineVariant = YadloPalette.lineLight,
    )

private val DarkMaterialColorScheme =
    darkColorScheme(
        primary = YadloPalette.primaryDark,
        onPrimary = YadloPalette.inkLight,
        primaryContainer = YadloPalette.brandDark,
        onPrimaryContainer = YadloPalette.onBrandDark,
        secondary = YadloPalette.accent,
        onSecondary = YadloPalette.inkLight,
        background = YadloPalette.backgroundDark,
        onBackground = YadloPalette.inkDark,
        surface = YadloPalette.backgroundDark,
        onSurface = YadloPalette.inkDark,
        surfaceVariant = YadloPalette.surfaceDark,
        onSurfaceVariant = YadloPalette.inkMutedDark,
        outline = YadloPalette.inkSoftDark,
        outlineVariant = YadloPalette.lineDark,
    )

/** Root theme composable. Wire any additional domain color layer here alongside LocalAppColors. */
@Composable
fun YadloTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val appColors = if (darkTheme) DarkAppColors else LightAppColors
    val materialColors = if (darkTheme) DarkMaterialColorScheme else LightMaterialColorScheme

    MaterialTheme(
        colorScheme = materialColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = {
            CompositionLocalProvider(
                LocalSpacing provides Spacing(),
                LocalYadloColors provides appColors,
            ) {
                content()
            }
        },
    )
}
