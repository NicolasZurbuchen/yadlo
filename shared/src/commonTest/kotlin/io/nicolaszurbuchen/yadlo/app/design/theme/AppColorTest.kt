package io.nicolaszurbuchen.yadlo.app.design.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class AppColorTest {
    // Asserting hex values would only detect change, not defects. What is worth holding is the
    // set of relationships the identity was chosen for: legible pairings, and category colours
    // that stay apart from each other.

    // region contrast

    @Test
    fun brand_carriesInkThatMeetsWcagAa() {
        // The website puts white on #74AEE0, which is 2.4:1 and fails. Dark ink on the same blue
        // is 5.4:1. This is the single decision most likely to be undone by someone "fixing" the
        // bandeau to match the site, so it is asserted rather than left as prose in SPEC.
        assertMeetsAa(LightAppColors.onBrand, LightAppColors.brand, "light brand")
        assertMeetsAa(DarkAppColors.onBrand, DarkAppColors.brand, "dark brand")
    }

    @Test
    fun accent_carriesInkThatMeetsWcagAa() {
        assertMeetsAa(LightAppColors.onAccent, LightAppColors.accent, "light accent")
        assertMeetsAa(DarkAppColors.onAccent, DarkAppColors.accent, "dark accent")
    }

    @Test
    fun bodyText_meetsWcagAaOnItsBackground() {
        assertMeetsAa(LightAppColors.textPrimary, LightAppColors.background, "light body")
        assertMeetsAa(DarkAppColors.textPrimary, DarkAppColors.background, "dark body")
        assertMeetsAa(LightAppColors.textPrimary, LightAppColors.surface, "light body on surface")
        assertMeetsAa(DarkAppColors.textPrimary, DarkAppColors.surface, "dark body on surface")
    }

    // endregion

    // region category separation

    @Test
    fun categoryColours_areAllDistinct_withinATheme() {
        // The whole point of the measured palette is that two dots are told apart at arm's length
        // in sunlight. Two categories sharing a value would defeat it silently.
        listOf(LightAppColors to "light", DarkAppColors to "dark").forEach { (colors, name) ->
            val measured =
                with(colors.category) { listOf(musique, eau, terre, enfants, silent) }

            assertEquals(measured.size, measured.distinct().size, "$name: two categories share a colour")
        }
    }

    @Test
    fun categoryColours_differ_betweenLightAndDark() {
        // Dark is a lifted, desaturated set, not the light set reused - the light values sit too
        // close to the dark background to stay legible.
        assertNotEquals(LightAppColors.category.musique, DarkAppColors.category.musique)
        assertNotEquals(LightAppColors.category.eau, DarkAppColors.category.eau)
        assertNotEquals(LightAppColors.category.terre, DarkAppColors.category.terre)
        assertNotEquals(LightAppColors.category.enfants, DarkAppColors.category.enfants)
        assertNotEquals(LightAppColors.category.silent, DarkAppColors.category.silent)
    }

    // endregion

    // region resolving a content id

    @Test
    fun forId_measuredCategories_resolveToTheirOwnColour() {
        with(LightAppColors.category) {
            assertEquals(musique, forId("musique"))
            assertEquals(eau, forId("eau"))
            assertEquals(terre, forId("terre"))
            assertEquals(enfants, forId("enfants"))
            assertEquals(silent, forId("silent"))
        }
    }

    @Test
    fun forId_categoriesWithoutAMeasuredColour_resolveToNeutral() {
        // restauration and createurs exist in the 2026 content but were added after the identity
        // was fixed, so they have no measured hue. Neutral is deliberate, not a missing case.
        with(LightAppColors.category) {
            assertEquals(neutral, forId("restauration"))
            assertEquals(neutral, forId("createurs"))
        }
    }

    @Test
    fun forId_unknownCategory_resolvesToNeutralRatherThanFailing() {
        // The content may add a category before the app has a colour for it. A neutral chip beats
        // a crash, and beats defaulting onto a hue that already means something else.
        assertEquals(LightAppColors.category.neutral, LightAppColors.category.forId("kayak"))
    }

    // endregion

    private fun assertMeetsAa(
        foreground: Color,
        background: Color,
        label: String,
    ) {
        val ratio = contrastRatio(foreground, background)

        assertTrue(ratio >= WCAG_AA_NORMAL_TEXT, "$label: contrast is ${ratio.format()}:1, below $WCAG_AA_NORMAL_TEXT:1")
    }

    private fun contrastRatio(
        a: Color,
        b: Color,
    ): Double {
        val la = relativeLuminance(a)
        val lb = relativeLuminance(b)

        return (max(la, lb) + 0.05) / (min(la, lb) + 0.05)
    }

    /** WCAG 2.1 relative luminance, sRGB. */
    private fun relativeLuminance(color: Color): Double {
        fun linear(component: Float): Double {
            val c = component.toDouble()
            return if (c <= 0.03928) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
        }

        return 0.2126 * linear(color.red) + 0.7152 * linear(color.green) + 0.0722 * linear(color.blue)
    }

    private fun Double.format(): String = ((this * 100).toInt() / 100.0).toString()

    private companion object {
        // WCAG 2.1 AA for text below 18pt. The app is read outdoors in July sun, so this is a
        // floor rather than a target.
        const val WCAG_AA_NORMAL_TEXT = 4.5
    }
}
