package io.nicolaszurbuchen.yadlo.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

// Material's scheme is mapped from AppColors rather than from the palettes a second time. Anything
// Material draws for us — a NavigationBar, a Button, a ripple — has to land on the identity without
// every call site overriding it, and reading the semantic layer here is what stops the two drifting
// into disagreement about what "primary" means. Beyond this file, read AppColors instead.
private val LightMaterialColorScheme =
    with(LightAppColors) {
        lightColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            primaryContainer = primarySubtle,
            onPrimaryContainer = onPrimarySubtle,
            secondary = accent,
            onSecondary = onAccent,
            secondaryContainer = accentSubtle,
            onSecondaryContainer = onAccentSubtle,
            background = background,
            onBackground = textPrimary,
            surface = surface,
            onSurface = textPrimary,
            surfaceVariant = surfaceRaised,
            onSurfaceVariant = textSecondary,
            outline = borderStrong,
            outlineVariant = borderSubtle,
        )
    }

private val DarkMaterialColorScheme =
    with(DarkAppColors) {
        darkColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            primaryContainer = primarySubtle,
            onPrimaryContainer = onPrimarySubtle,
            secondary = accent,
            onSecondary = onAccent,
            secondaryContainer = accentSubtle,
            onSecondaryContainer = onAccentSubtle,
            background = background,
            onBackground = textPrimary,
            surface = surface,
            onSurface = textPrimary,
            surfaceVariant = surfaceRaised,
            onSurfaceVariant = textSecondary,
            outline = borderStrong,
            outlineVariant = borderSubtle,
        )
    }

/** Root theme composable. Wire any additional domain colour layer here alongside LocalAppColors. */
@Composable
fun YadloTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val appColors = if (darkTheme) DarkAppColors else LightAppColors
    val categoryColors = if (darkTheme) DarkCategoryColors else LightCategoryColors
    val materialColors = if (darkTheme) DarkMaterialColorScheme else LightMaterialColorScheme

    MaterialTheme(
        colorScheme = materialColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = {
            CompositionLocalProvider(
                LocalSpacing provides Spacing(),
                LocalAppColors provides appColors,
                LocalCategoryColors provides categoryColors,
            ) {
                content()
            }
        },
    )
}
