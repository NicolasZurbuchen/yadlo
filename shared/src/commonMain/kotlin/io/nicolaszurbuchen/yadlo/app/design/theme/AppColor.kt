package io.nicolaszurbuchen.yadlo.app.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** App-level semantic color layer, sitting on top of Material's ColorScheme. */
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
    val category: CategoryColors,
    val isDark: Boolean,
)

/**
 * One colour per Category.
 *
 * Category ids come from the content, but their colours live here — a colour is a design decision
 * made once against a measured palette, not a field an editor should be able to change.
 *
 * Only five of the seven categories have a measured colour. `restauration` and `createurs` were
 * added to the content after the identity was fixed and were never part of the ΔE measurement, so
 * they deliberately resolve to [neutral] rather than to an invented sixth and seventh hue. That is
 * also consistent with the stand list, where category colour was removed on purpose.
 */
data class CategoryColors(
    val musique: Color,
    val eau: Color,
    val terre: Color,
    val enfants: Color,
    val silent: Color,
    val neutral: Color,
) {
    /**
     * Resolves a Category id from the content. An unknown id is not an error: the content may add
     * a category before the app has a colour for it, and a neutral chip is a far better outcome
     * than a crash or a default that collides with a measured hue.
     */
    fun forId(categoryId: String): Color =
        when (categoryId) {
            "musique" -> musique
            "eau" -> eau
            "terre" -> terre
            "enfants" -> enfants
            "silent" -> silent
            else -> neutral
        }
}

val DarkAppColors =
    AppColors(
        isDark = true,
        background = YadloPalette.backgroundDark,
        surface = YadloPalette.surfaceDark,
        surfaceRaised = YadloPalette.lineDark,
        borderSubtle = YadloPalette.lineDark,
        borderDefault = YadloPalette.inkSoftDark,
        textPrimary = YadloPalette.inkDark,
        textSecondary = YadloPalette.inkMutedDark,
        textTertiary = YadloPalette.inkSoftDark,
        textDisabled = YadloPalette.inkSoftDark,
        textInverse = YadloPalette.inkLight,
        accent = YadloPalette.accent,
        onAccent = YadloPalette.inkLight,
        accentSubtle = YadloPalette.surfaceDark,
        onAccentSubtle = YadloPalette.inkDark,
        brand = YadloPalette.brandDark,
        onBrand = YadloPalette.onBrandDark,
        category =
            CategoryColors(
                musique = YadloPalette.musiqueDark,
                eau = YadloPalette.eauDark,
                terre = YadloPalette.terreDark,
                enfants = YadloPalette.enfantsDark,
                silent = YadloPalette.silentDark,
                neutral = YadloPalette.inkMutedDark,
            ),
    )

val LightAppColors =
    AppColors(
        isDark = false,
        background = YadloPalette.backgroundLight,
        surface = YadloPalette.surfaceLight,
        surfaceRaised = YadloPalette.backgroundLight,
        borderSubtle = YadloPalette.lineLight,
        borderDefault = YadloPalette.inkSoftLight,
        textPrimary = YadloPalette.inkLight,
        textSecondary = YadloPalette.inkMutedLight,
        textTertiary = YadloPalette.inkSoftLight,
        textDisabled = YadloPalette.inkSoftLight,
        textInverse = YadloPalette.backgroundLight,
        // Dark ink on the pink accent, for the same reason the brand blue carries navy.
        accent = YadloPalette.accent,
        onAccent = YadloPalette.inkLight,
        accentSubtle = YadloPalette.surfaceLight,
        onAccentSubtle = YadloPalette.inkLight,
        brand = YadloPalette.brandLight,
        onBrand = YadloPalette.onBrandLight,
        category =
            CategoryColors(
                musique = YadloPalette.musiqueLight,
                eau = YadloPalette.eauLight,
                terre = YadloPalette.terreLight,
                enfants = YadloPalette.enfantsLight,
                silent = YadloPalette.silentLight,
                neutral = YadloPalette.inkMutedLight,
            ),
    )

internal val LocalYadloColors = staticCompositionLocalOf { LightAppColors }

val MaterialTheme.appColors: AppColors
    @Composable
    @ReadOnlyComposable
    get() = LocalYadloColors.current
