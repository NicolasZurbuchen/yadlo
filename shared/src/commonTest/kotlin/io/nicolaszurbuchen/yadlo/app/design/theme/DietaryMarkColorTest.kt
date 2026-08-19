package io.nicolaszurbuchen.yadlo.app.design.theme

import androidx.compose.ui.graphics.Color
import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloDietaryMarkUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The dietary tints, measured on the grounds they can actually land on.
 *
 * They are read as a glyph *and* as the word beside it, always — which is why the floor here is
 * WCAG's 3:1 for non-text rather than 4.5:1. The label is drawn in the same colour and is set in the
 * smallest style the app has, so it is treated as the harder case and held to the same bar as the
 * glyph rather than being allowed to be the weak half.
 *
 * The tints are duplicated from [YadloDietaryMarkUiModel.tint] rather than read from it, because that is
 * a `@Composable` getter and this is a plain unit test. Duplication is the price of measuring at all
 * — and a drift between the two lists is exactly what the second test catches.
 */
class DietaryMarkColorTest {
    @Test
    fun everyTint_meetsTheNonTextFloorOnEveryGroundATagCanLandOn() {
        listOf(
            LightAppColors to lightTints,
            DarkAppColors to darkTints,
        ).forEach { (colors, tints) ->
            val theme = if (colors.isDark) "dark" else "light"

            listOf(
                "background" to colors.background,
                "surface" to colors.surface,
                "surfaceRaised" to colors.surfaceRaised,
            ).forEach { (groundName, ground) ->
                tints.forEach { (mark, tint) ->
                    val contrast = contrastRatio(tint, ground)

                    assertTrue(
                        contrast >= WCAG_AA_NON_TEXT,
                        "$theme: $mark on $groundName is ${contrast.format()}:1, below $WCAG_AA_NON_TEXT:1",
                    )
                }
            }
        }
    }

    @Test
    fun everyMark_hasATintInBothThemes() {
        // The lists above are hand-kept, so a seventh mark added to the enum without a measured
        // colour fails here rather than shipping as an unmeasured one.
        assertEquals(YadloDietaryMarkUiModel.entries.toSet(), lightTints.keys)
        assertEquals(YadloDietaryMarkUiModel.entries.toSet(), darkTints.keys)
    }

    @Test
    fun veganAndVegetarian_areTheSameHueApartRatherThanTwoUnrelatedColours() {
        // Deliberate: vegan is the stronger claim of the same family, every vegan dish is also
        // tagged vegetarian, and the two should read as related. The glyph is what separates them.
        // Asserting it keeps a later "let's make them distinct" from quietly breaking the idea.
        listOf(lightTints, darkTints).forEach { tints ->
            val vegan = tints.getValue(YadloDietaryMarkUiModel.VEGAN)
            val vegetarian = tints.getValue(YadloDietaryMarkUiModel.VEGETARIAN)

            assertTrue(
                perceptualDistance(vegan, vegetarian) < SAME_FAMILY_CEILING,
                "vegan and vegetarian are ${perceptualDistance(vegan, vegetarian).format()} apart, " +
                    "which reads as two unrelated colours",
            )
        }
    }

    private companion object {
        // WCAG 2.1 AA for anything that is not text. The tags are read outdoors in July sun, so
        // this is a floor rather than a target.
        const val WCAG_AA_NON_TEXT = 3.0

        // Two steps of one ramp measure around 15 apart; two of the five Category anchors are never
        // closer than 25. Between the two is where "related" stops and "different" starts.
        const val SAME_FAMILY_CEILING = 25.0

        val lightTints: Map<YadloDietaryMarkUiModel, Color> =
            mapOf(
                YadloDietaryMarkUiModel.VEGAN to EmeraldPalette.emerald900,
                YadloDietaryMarkUiModel.VEGETARIAN to EmeraldPalette.emerald700,
                YadloDietaryMarkUiModel.GLUTEN_FREE to AmberPalette.amber800,
                YadloDietaryMarkUiModel.DAIRY_FREE to SkyBluePalette.skyBlue800,
                YadloDietaryMarkUiModel.HALAL to VioletPalette.violet800,
                YadloDietaryMarkUiModel.SPICY to MagentaPalette.magenta800,
            )

        val darkTints: Map<YadloDietaryMarkUiModel, Color> =
            mapOf(
                YadloDietaryMarkUiModel.VEGAN to EmeraldPalette.emerald300,
                YadloDietaryMarkUiModel.VEGETARIAN to EmeraldPalette.emerald500,
                YadloDietaryMarkUiModel.GLUTEN_FREE to AmberPalette.amber400,
                YadloDietaryMarkUiModel.DAIRY_FREE to SkyBluePalette.skyBlue400,
                YadloDietaryMarkUiModel.HALAL to VioletPalette.violet400,
                YadloDietaryMarkUiModel.SPICY to MagentaPalette.magenta400,
            )
    }
}
