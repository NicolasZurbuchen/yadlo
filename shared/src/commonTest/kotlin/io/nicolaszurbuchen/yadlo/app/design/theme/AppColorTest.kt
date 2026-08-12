package io.nicolaszurbuchen.yadlo.app.design.theme

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertTrue

class AppColorTest {
    // Asserting hex values would only detect change, not defects. What is worth holding is the set
    // of relationships the identity was chosen for — every pairing the theme actually produces
    // staying legible, in both themes.

    @Test
    fun brand_carriesInkThatMeetsWcagAa() {
        // The website puts white on #74AEE0, which is 2.4:1 and fails. Dark ink on the same blue is
        // 6.7:1. This is the single decision most likely to be undone by someone "fixing" the
        // bandeau to match the site, so it is asserted rather than left as prose in SPEC.
        assertMeetsAa(LightAppColors.onBrand, LightAppColors.brand, "light brand")
        assertMeetsAa(DarkAppColors.onBrand, DarkAppColors.brand, "dark brand")
    }

    @Test
    fun accent_carriesInkThatMeetsWcagAa() {
        assertMeetsAa(LightAppColors.onAccent, LightAppColors.accent, "light accent")
        assertMeetsAa(DarkAppColors.onAccent, DarkAppColors.accent, "dark accent")
        assertMeetsAa(LightAppColors.onAccentSubtle, LightAppColors.accentSubtle, "light accent subtle")
        assertMeetsAa(DarkAppColors.onAccentSubtle, DarkAppColors.accentSubtle, "dark accent subtle")
    }

    @Test
    fun everyTextRole_meetsWcagAaOnEverySurfaceItCanLandOn() {
        // textDisabled is deliberately excluded: it marks a control that cannot be used, and WCAG
        // exempts inactive components. Every other text role has to survive July sun on any of the
        // three grounds, so they are checked as a matrix rather than one representative pair.
        listOf(LightAppColors to "light", DarkAppColors to "dark").forEach { (colors, theme) ->
            val texts =
                listOf(
                    "textPrimary" to colors.textPrimary,
                    "textSecondary" to colors.textSecondary,
                    "textTertiary" to colors.textTertiary,
                )
            val grounds =
                listOf(
                    "background" to colors.background,
                    "surface" to colors.surface,
                    "surfaceRaised" to colors.surfaceRaised,
                )

            texts.forEach { (textName, text) ->
                grounds.forEach { (groundName, ground) ->
                    assertMeetsAa(text, ground, "$theme: $textName on $groundName")
                }
            }
        }
    }

    @Test
    fun textInverse_meetsWcagAaOnTheOppositeThemesGround() {
        // textInverse exists for the one place a light theme goes dark and vice versa - a snackbar,
        // a scrim over the fiche photo. Read against its own theme's ground it would be invisible,
        // which is the point, so it is checked against the inverse.
        assertMeetsAa(LightAppColors.textInverse, LightAppColors.textPrimary, "light textInverse")
        assertMeetsAa(DarkAppColors.textInverse, DarkAppColors.textPrimary, "dark textInverse")
    }

    private fun assertMeetsAa(
        foreground: Color,
        background: Color,
        label: String,
    ) {
        val ratio = contrastRatio(foreground, background)

        assertTrue(
            ratio >= WCAG_AA_NORMAL_TEXT,
            "$label: contrast is ${ratio.format()}:1, below $WCAG_AA_NORMAL_TEXT:1",
        )
    }

    private companion object {
        // WCAG 2.1 AA for text below 18pt. The app is read outdoors in July sun, so this is a floor
        // rather than a target.
        const val WCAG_AA_NORMAL_TEXT = 4.5
    }
}
