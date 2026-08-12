package io.nicolaszurbuchen.yadlo.app.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * The Category colour layer — one colour per kind of Happening.
 *
 * A domain layer rather than fields on [AppColors] because nothing outside the programme has any
 * use for "the colour of eau": a generic screen never asks for it.
 *
 * Category ids come from the content, their colours do not. Colour is a design decision made once
 * against a measured palette, not a field an editor should be able to change — and it is never the
 * only carrier of meaning, since every Category is also written out in words.
 *
 * Only five of the seven Categories have a measured colour. `restauration` and `createurs` were
 * added to the content after the identity was fixed and were never part of the perceptual-separation
 * measurement, so they resolve to [neutral] rather than to an invented sixth and seventh hue. That
 * also matches the stand list, where category colour was removed on purpose.
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
     * Resolves a Category id from the content. An unknown id is not an error: the content may add a
     * Category before the app has a colour for it, and a neutral chip is a far better outcome than a
     * crash or a default that collides with a measured hue.
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

val LightCategoryColors =
    CategoryColors(
        musique = MagentaPalette.magenta600,
        eau = SkyBluePalette.skyBlue600,
        terre = EmeraldPalette.emerald600,
        enfants = AmberPalette.amber500,
        silent = VioletPalette.violet600,
        // Neutral has to stay clear of the measured five, not just look muted: a lighter slate sits
        // close enough to eau that a restauration chip reads as a water activity.
        neutral = SlatePalette.slate700,
    )

/**
 * Dark is a lifted set, not the light one reused — the light values sit too close to the dark
 * ground to stay legible. The steps are uneven on purpose: they were picked as the combination that
 * keeps all six furthest apart from each other while every one still clears 4.5:1 on the dark
 * background, rather than by lifting them all by the same amount, which separates them worse.
 */
val DarkCategoryColors =
    CategoryColors(
        musique = MagentaPalette.magenta400,
        eau = SkyBluePalette.skyBlue300,
        terre = EmeraldPalette.emerald500,
        enfants = AmberPalette.amber400,
        silent = VioletPalette.violet500,
        neutral = SlatePalette.slate500,
    )

internal val LocalCategoryColors = staticCompositionLocalOf { LightCategoryColors }

val MaterialTheme.categoryColors: CategoryColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCategoryColors.current
