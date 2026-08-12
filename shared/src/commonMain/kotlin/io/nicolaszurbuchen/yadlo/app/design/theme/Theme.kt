package io.nicolaszurbuchen.yadlo.app.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

// Material's scheme is mapped from the same palettes rather than being a second source of colour.
// Anything Material draws for us — a NavigationBar, a Button, a ripple — has to land on the
// identity without every call site overriding it. Beyond this file, read AppColors instead.
private val LightMaterialColorScheme =
    lightColorScheme(
        primary = SkyBluePalette.skyBlue800,
        onPrimary = Color.White,
        primaryContainer = SkyBluePalette.skyBlue400,
        onPrimaryContainer = SlatePalette.slate900,
        secondary = RosePalette.rose400,
        onSecondary = SlatePalette.slate900,
        background = SlatePalette.slate50,
        onBackground = SlatePalette.slate900,
        surface = Color.White,
        onSurface = SlatePalette.slate900,
        surfaceVariant = SlatePalette.slate100,
        onSurfaceVariant = SlatePalette.slate700,
        outline = SlatePalette.slate400,
        outlineVariant = SlatePalette.slate200,
    )

private val DarkMaterialColorScheme =
    darkColorScheme(
        primary = SkyBluePalette.skyBlue400,
        onPrimary = SlatePalette.slate950,
        primaryContainer = SkyBluePalette.skyBlue900,
        onPrimaryContainer = SkyBluePalette.skyBlue200,
        secondary = RosePalette.rose400,
        onSecondary = SlatePalette.slate950,
        background = SlatePalette.slate950,
        onBackground = SlatePalette.slate100,
        surface = SlatePalette.slate900,
        onSurface = SlatePalette.slate100,
        surfaceVariant = SlatePalette.slate800,
        onSurfaceVariant = SlatePalette.slate300,
        outline = SlatePalette.slate600,
        outlineVariant = SlatePalette.slate700,
    )

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
