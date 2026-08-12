package io.nicolaszurbuchen.yadlo.app.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * App-level semantic colour layer, sitting on top of Material's ColorScheme. This is what our own
 * composables read; `colorScheme` exists only so stock Material components have a sane default.
 *
 * [brand] is the bandeau blue from yadlo.ch and always carries dark ink: the site's own
 * white-on-#74AEE0 is 2.4:1, while navy on the same blue is 6.7:1.
 */
data class AppColors(
    val background: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val borderSubtle: Color,
    val borderDefault: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textDisabled: Color,
    val textInverse: Color,
    val accent: Color,
    val onAccent: Color,
    val accentSubtle: Color,
    val onAccentSubtle: Color,
    val brand: Color,
    val onBrand: Color,
    val isDark: Boolean,
)

val DarkAppColors =
    AppColors(
        isDark = true,
        background = SlatePalette.slate950,
        surface = SlatePalette.slate900,
        surfaceRaised = SlatePalette.slate800,
        borderSubtle = SlatePalette.slate800,
        borderDefault = SlatePalette.slate700,
        textPrimary = SlatePalette.slate100,
        textSecondary = SlatePalette.slate200,
        // slate400 was the obvious tertiary and it fails: 4.45:1 on surfaceRaised, which is the one
        // ground of the three where a dim text role is most likely to be used.
        textTertiary = SlatePalette.slate300,
        textDisabled = SlatePalette.slate600,
        textInverse = SlatePalette.slate950,
        accent = RosePalette.rose400,
        onAccent = SlatePalette.slate950,
        accentSubtle = RosePalette.rose900,
        onAccentSubtle = RosePalette.rose200,
        // The bandeau inverts in dark: the blue becomes the deep step and carries pale ink, rather
        // than staying light and forcing a bright band into a dark screen at 01:00.
        brand = SkyBluePalette.skyBlue900,
        onBrand = SkyBluePalette.skyBlue200,
    )

val LightAppColors =
    AppColors(
        isDark = false,
        background = SlatePalette.slate50,
        surface = Color.White,
        surfaceRaised = SlatePalette.slate100,
        borderSubtle = SlatePalette.slate100,
        borderDefault = SlatePalette.slate200,
        textPrimary = SlatePalette.slate900,
        textSecondary = SlatePalette.slate700,
        textTertiary = SlatePalette.slate600,
        textDisabled = SlatePalette.slate400,
        textInverse = SlatePalette.slate50,
        accent = RosePalette.rose400,
        // Dark ink on the pink accent, for the same reason the brand blue carries navy.
        onAccent = SlatePalette.slate900,
        accentSubtle = RosePalette.rose100,
        onAccentSubtle = RosePalette.rose900,
        brand = SkyBluePalette.skyBlue400,
        onBrand = SlatePalette.slate900,
    )

internal val LocalAppColors = staticCompositionLocalOf { LightAppColors }

val MaterialTheme.appColors: AppColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current
