package io.nicolaszurbuchen.yadlo.app.design.theme

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertTrue

class AppColorTest {
    // Asserting hex values would only detect change, not defects. What is worth holding is the set
    // of relationships the identity was chosen for — every pairing the theme actually produces
    // staying legible, in both themes.

    @Test
    fun primarySubtle_carriesInkThatMeetsWcagAa() {
        // The website puts white on the bandeau blue, which is 2.4:1 and fails. Dark ink on the same
        // blue is 6.7:1. This is the single decision most likely to be undone by someone "fixing"
        // the blue to match the site, so it is asserted rather than left as prose in SPEC.
        assertMeetsAa(LightAppColors.onPrimarySubtle, LightAppColors.primarySubtle, "light primarySubtle")
        assertMeetsAa(DarkAppColors.onPrimarySubtle, DarkAppColors.primarySubtle, "dark primarySubtle")
    }

    @Test
    fun everyFilledRole_carriesInkThatMeetsWcagAa() {
        // The quads are the only places the app puts text on a saturated fill of its own choosing,
        // so each one is checked against the ink it ships with rather than a representative pair.
        listOf(LightAppColors to "light", DarkAppColors to "dark").forEach { (colors, theme) ->
            assertMeetsAa(colors.onPrimary, colors.primary, "$theme: onPrimary")
            assertMeetsAa(colors.onPrimarySubtle, colors.primarySubtle, "$theme: onPrimarySubtle")
            assertMeetsAa(colors.onAccent, colors.accent, "$theme: onAccent")
            assertMeetsAa(colors.onAccentSubtle, colors.accentSubtle, "$theme: onAccentSubtle")
            assertMeetsAa(colors.onLive, colors.live, "$theme: onLive")
            assertMeetsAa(colors.onWarning, colors.warning, "$theme: onWarning")
            assertMeetsAa(colors.onUrgent, colors.urgent, "$theme: onUrgent")
        }
    }

    @Test
    fun liveStateRoles_stayLegibleOutlinedAsWellAsFilled() {
        // `en cours` fills with live and `dans 45 min` writes in warning, so both have to clear the
        // bar as ink on the page grounds and not only against their own paired ink. The outlined
        // half is the one that gets forgotten.
        //
        // `urgent` is deliberately absent: it is only ever a fill, which is what lets it be the
        // vivid step of its ramp rather than a step deep enough to be written with. If anything
        // starts drawing `se termine` as text, it belongs in this list and will not pass.
        listOf(LightAppColors to "light", DarkAppColors to "dark").forEach { (colors, theme) ->
            listOf("live" to colors.live, "warning" to colors.warning).forEach { (roleName, role) ->
                listOf(
                    "background" to colors.background,
                    "surface" to colors.surface,
                    "surfaceRaised" to colors.surfaceRaised,
                ).forEach { (groundName, ground) ->
                    assertMeetsAa(role, ground, "$theme: $roleName on $groundName")
                }
            }
        }
    }

    @Test
    fun everyTextRole_meetsWcagAaOnEveryGroundItCanLandOn() {
        // Checked as a matrix rather than one representative pair: the roles are not ranked by which
        // ground they sit on, so any of the three can appear under any of them, and the pairing that
        // fails is never the one anyone thinks to check by hand.
        listOf(LightAppColors to "light", DarkAppColors to "dark").forEach { (colors, theme) ->
            val texts =
                listOf(
                    "textPrimary" to colors.textPrimary,
                    "textSecondary" to colors.textSecondary,
                    "textTertiary" to colors.textTertiary,
                    // Not a text role by name, but it is only ever drawn as text — see AppColors.
                    "accentInk" to colors.accentInk,
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
    fun thePolarityPair_meetsWcagAaOnEveryGroundAMarkCanLandOn() {
        // Held to the text floor rather than the 3:1 non-text one. A ✓ or a ✕ is the size of a word
        // and is read as one — it is not a border or a chart segment — and it is the whole answer on
        // the row it sits on. The dark half is where a green picked in light mode quietly fails.
        listOf(LightAppColors to "light", DarkAppColors to "dark").forEach { (colors, theme) ->
            listOf("positive" to colors.positive, "negative" to colors.negative).forEach { (roleName, role) ->
                listOf(
                    "background" to colors.background,
                    "surface" to colors.surface,
                    "surfaceRaised" to colors.surfaceRaised,
                ).forEach { (groundName, ground) ->
                    assertMeetsAa(role, ground, "$theme: $roleName on $groundName")
                }
            }
        }
    }

    @Test
    fun borderStrong_meetsTheNonTextFloorOnEveryGround() {
        // A drawn edge is a UI component, not text, so 3:1 is the bar rather than 4.5:1. It still
        // needs one: an unselected filter chip whose border cannot be seen is an invisible control.
        listOf(LightAppColors to "light", DarkAppColors to "dark").forEach { (colors, theme) ->
            listOf(
                "background" to colors.background,
                "surface" to colors.surface,
                "surfaceRaised" to colors.surfaceRaised,
            ).forEach { (groundName, ground) ->
                val ratio = contrastRatio(colors.borderStrong, ground)

                assertTrue(
                    ratio >= WCAG_AA_NON_TEXT,
                    "$theme: borderStrong on $groundName is ${ratio.format()}:1, below $WCAG_AA_NON_TEXT:1",
                )
            }
        }
    }

    @Test
    fun scrim_keepsWhiteTextLegibleOverTheBrightestPossiblePhoto() {
        // The scrim's whole job is to make an unvetted photograph a predictable ground. A fully
        // white image is the worst case it has to survive, and it is the case that never appears in
        // the press shots someone would check it against.
        val overWhite = LightAppColors.scrim.over(Color.White)

        assertMeetsAa(Color.White, overWhite, "scrim over a white photo")
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

        // WCAG 2.1 AA for a UI component's own boundary, which is held to a lower bar than text.
        const val WCAG_AA_NON_TEXT = 3.0
    }
}
