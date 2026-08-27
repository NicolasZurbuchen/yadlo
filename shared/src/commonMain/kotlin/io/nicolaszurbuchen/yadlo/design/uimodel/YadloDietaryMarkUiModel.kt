package io.nicolaszurbuchen.yadlo.design.uimodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import io.nicolaszurbuchen.yadlo.design.theme.AmberPalette
import io.nicolaszurbuchen.yadlo.design.theme.EmeraldPalette
import io.nicolaszurbuchen.yadlo.design.theme.MagentaPalette
import io.nicolaszurbuchen.yadlo.design.theme.SkyBluePalette
import io.nicolaszurbuchen.yadlo.design.theme.SlatePalette
import io.nicolaszurbuchen.yadlo.design.theme.VioletPalette
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import org.jetbrains.compose.resources.StringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.dietary_all_dairy_free
import yadlo.shared.generated.resources.dietary_all_gluten_free
import yadlo.shared.generated.resources.dietary_all_halal
import yadlo.shared.generated.resources.dietary_all_spicy
import yadlo.shared.generated.resources.dietary_all_vegan
import yadlo.shared.generated.resources.dietary_all_vegetarian
import yadlo.shared.generated.resources.dietary_mark_dairy_free
import yadlo.shared.generated.resources.dietary_mark_gluten_free
import yadlo.shared.generated.resources.dietary_mark_halal
import yadlo.shared.generated.resources.dietary_mark_spicy
import yadlo.shared.generated.resources.dietary_mark_vegan
import yadlo.shared.generated.resources.dietary_mark_vegetarian
import yadlo.shared.generated.resources.dietary_some_dairy_free
import yadlo.shared.generated.resources.dietary_some_gluten_free
import yadlo.shared.generated.resources.dietary_some_halal
import yadlo.shared.generated.resources.dietary_some_spicy
import yadlo.shared.generated.resources.dietary_some_vegan
import yadlo.shared.generated.resources.dietary_some_vegetarian

/**
 * The six things a dish can be, with the glyph and the colour that say so.
 *
 * **A closed set in the app, keyed by an id in the content** — exactly the arrangement
 * [io.nicolaszurbuchen.yadlo.design.theme.CategoryColors] uses, and for the same reason: the
 * vocabulary is a content decision, the way it looks is a design one made once against a measured
 * palette. The ids are the slugs `content/SCHEMA.md` declares, not the French words a reader sees,
 * because a lookup key must be one byte sequence and `végan` is two depending on who typed it.
 *
 * **The label is never carried by the colour alone.** Every tag is written out beside its glyph, and
 * the glyph is a shape before it is a hue, because the whole point of these is to be read by someone
 * who cannot eat what they get wrong.
 *
 * [VEGAN] and [VEGETARIAN] are deliberately the same green two steps apart, rather than two unrelated
 * hues: the first is the stronger claim of the same family, every vegan dish is also tagged
 * vegetarian, and the two appearing together should read as related. The glyph is what separates
 * them at a glance. The other four take a hue each — wheat gold, milk blue, and the hottest colour
 * the palette has for the one that will make you reach for a drink.
 *
 * There is no `bio`. It was a claim about sourcing rather than about whether someone can eat the
 * thing, and it was the one mark the filter could not answer a question with.
 */
enum class YadloDietaryMarkUiModel(
    val id: String,
    val icon: ImageVector,
    /** On one dish. */
    val label: StringResource,
    /** On a Stand where every dish carries it. */
    val allLabel: StringResource,
    /** On a Stand where only some do. */
    val someLabel: StringResource,
) {
    VEGAN("vegan", Icons.Outlined.Eco, Res.string.dietary_mark_vegan, Res.string.dietary_all_vegan, Res.string.dietary_some_vegan),

    VEGETARIAN(
        "vegetarien",
        Icons.Outlined.Spa,
        Res.string.dietary_mark_vegetarian,
        Res.string.dietary_all_vegetarian,
        Res.string.dietary_some_vegetarian,
    ),

    GLUTEN_FREE(
        "sans-gluten",
        Icons.Outlined.Grain,
        Res.string.dietary_mark_gluten_free,
        Res.string.dietary_all_gluten_free,
        Res.string.dietary_some_gluten_free,
    ),

    DAIRY_FREE(
        "sans-lactose",
        Icons.Outlined.WaterDrop,
        Res.string.dietary_mark_dairy_free,
        Res.string.dietary_all_dairy_free,
        Res.string.dietary_some_dairy_free,
    ),

    HALAL("halal", Icons.Outlined.Verified, Res.string.dietary_mark_halal, Res.string.dietary_all_halal, Res.string.dietary_some_halal),

    SPICY(
        "piquant",
        Icons.Outlined.LocalFireDepartment,
        Res.string.dietary_mark_spicy,
        Res.string.dietary_all_spicy,
        Res.string.dietary_some_spicy,
    ),
    ;

    /**
     * Measured rather than picked: every value clears 3:1 — the WCAG floor for a glyph, which is not
     * text — on all three grounds of its own theme. DietaryMarkTest holds that rather than this
     * comment doing it.
     *
     * Dark is a lifted set rather than the light one reused, for the reason `CategoryColors` gives:
     * the light steps are too deep to be seen against a dark ground at all.
     */
    val tint: Color
        @Composable
        @ReadOnlyComposable
        get() =
            if (MaterialTheme.appColors.isDark) {
                when (this) {
                    VEGAN -> EmeraldPalette.emerald300
                    VEGETARIAN -> EmeraldPalette.emerald500
                    GLUTEN_FREE -> AmberPalette.amber400
                    DAIRY_FREE -> SkyBluePalette.skyBlue400
                    HALAL -> VioletPalette.violet400
                    SPICY -> MagentaPalette.magenta400
                }
            } else {
                when (this) {
                    VEGAN -> EmeraldPalette.emerald900
                    VEGETARIAN -> EmeraldPalette.emerald700
                    GLUTEN_FREE -> AmberPalette.amber800
                    DAIRY_FREE -> SkyBluePalette.skyBlue800
                    HALAL -> VioletPalette.violet800
                    SPICY -> MagentaPalette.magenta800
                }
            }

    /**
     * The ink a chip filled with [tint] carries.
     *
     * Uniform per theme rather than per mark, unlike `CategoryColors`, and measured rather than
     * assumed: the light tints are all deep enough to carry white — the closest is vegetarian at
     * 4.9:1 — and the dark tints are all light enough to carry the near-black ground. That is a
     * property of having picked one end of each ramp per theme rather than a coincidence, and
     * DietaryMarkColorTest holds it.
     */
    val ink: Color
        @Composable
        @ReadOnlyComposable
        get() = if (MaterialTheme.appColors.isDark) SlatePalette.slate950 else Color.White

    companion object {
        /**
         * Null for an id this build has no answer for. The content can publish a seventh mark before
         * the app has a glyph and a colour for it, and dropping one tag is a far better outcome than
         * a crash or an untinted glyph nobody can read.
         */
        fun forId(id: String): YadloDietaryMarkUiModel? = entries.firstOrNull { it.id == id }
    }
}
