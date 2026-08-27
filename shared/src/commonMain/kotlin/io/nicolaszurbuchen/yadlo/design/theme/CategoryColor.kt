package io.nicolaszurbuchen.yadlo.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * A Category's fill and the ink that stays legible on it.
 *
 * The two travel together because the right ink is a property of the individual hue rather than of
 * the theme: in light, `enfants` yellow carries dark ink at 8.4:1 while `musique` magenta carries
 * white at 4.2:1. A single `onCategory` field would be wrong for one of them whichever value it
 * took, and the fiche needs the pair — its collapsing toolbar takes the Category colour and carries
 * the title on top of it.
 */
data class CategoryColor(
    val fill: Color,
    val ink: Color,
)

/**
 * The Category colour layer — one [CategoryColor] per kind of Happening.
 *
 * A domain layer rather than fields on [AppColors] because nothing outside the programme has any use
 * for "the colour of water": a generic screen never asks for it.
 *
 * Category ids come from the content, their colours do not. Colour is a design decision made once
 * against a measured palette, not a field an editor should be able to change — and it is never the
 * only carrier of meaning, since every Category is also written out in words.
 *
 * Only five of the seven Categories have a measured colour. `restauration` and `createurs` were
 * added to the content after the identity was fixed and were never part of the perceptual-separation
 * measurement, so they resolve to [neutral] rather than to an invented sixth and seventh hue. That
 * also matches the stand list, where Category colour was removed on purpose.
 */
data class CategoryColors(
    val music: CategoryColor,
    val water: CategoryColor,
    val land: CategoryColor,
    val children: CategoryColor,
    val silent: CategoryColor,
    val neutral: CategoryColor,
) {
    /**
     * Resolves a Category id from the content. An unknown id is not an error: the content may add a
     * Category before the app has a colour for it, and a neutral chip is a far better outcome than a
     * crash or a default that collides with a measured hue.
     */
    fun forId(categoryId: String): CategoryColor =
        // The ids are French because the content is French (CONTEXT.md § Category). They arrive from
        // the edition bundle as data, so they are string literals here rather than names in this
        // codebase — which is why the properties they map onto are English.
        when (categoryId) {
            "musique" -> music
            "eau" -> water
            "terre" -> land
            "enfants" -> children
            "silent" -> silent
            else -> neutral
        }
}

/**
 * Light inks are mixed — white on three fills, dark ink on two — because the anchors were chosen for
 * separation from each other, not for a common ink. Each is the better of the two options for its
 * own fill, measured rather than eyeballed.
 */
val LightCategoryColors =
    CategoryColors(
        music = CategoryColor(fill = MagentaPalette.magenta600, ink = Color.White),
        water = CategoryColor(fill = SkyBluePalette.skyBlue600, ink = Color.White),
        land = CategoryColor(fill = EmeraldPalette.emerald600, ink = SlatePalette.slate900),
        children = CategoryColor(fill = AmberPalette.amber500, ink = SlatePalette.slate900),
        silent = CategoryColor(fill = VioletPalette.violet600, ink = Color.White),
        // Neutral has to stay clear of the measured five, not merely look muted: a lighter slate
        // sits close enough to water that a restauration chip reads as a water activity.
        neutral = CategoryColor(fill = SlatePalette.slate700, ink = Color.White),
    )

/**
 * Dark is a lifted set, not the light one reused — the light values sit too close to the dark ground
 * to stay legible. The steps are uneven on purpose: they were picked as the combination that keeps
 * all six furthest apart from each other while every one still clears 4.5:1 on the dark background,
 * rather than by lifting them all by the same amount, which separates them worse.
 *
 * Every dark fill is light enough to carry dark ink, so unlike light the inks are uniform here.
 */
val DarkCategoryColors =
    CategoryColors(
        music = CategoryColor(fill = MagentaPalette.magenta400, ink = SlatePalette.slate950),
        water = CategoryColor(fill = SkyBluePalette.skyBlue300, ink = SlatePalette.slate950),
        land = CategoryColor(fill = EmeraldPalette.emerald500, ink = SlatePalette.slate950),
        children = CategoryColor(fill = AmberPalette.amber400, ink = SlatePalette.slate950),
        silent = CategoryColor(fill = VioletPalette.violet500, ink = SlatePalette.slate950),
        neutral = CategoryColor(fill = SlatePalette.slate500, ink = SlatePalette.slate950),
    )

internal val LocalCategoryColors = staticCompositionLocalOf { LightCategoryColors }

val MaterialTheme.categoryColors: CategoryColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCategoryColors.current
