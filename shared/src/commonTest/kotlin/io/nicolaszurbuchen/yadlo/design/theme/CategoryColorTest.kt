package io.nicolaszurbuchen.yadlo.design.theme

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CategoryColorTest {
    @Test
    fun everyCategory_staysPerceptuallyApartFromEveryOther_withinATheme() {
        // The whole point of the measured palette is that two dots are told apart at arm's length in
        // sunlight. Asserting they merely differ would pass on two greens a pixel apart, so the
        // floor is a perceptual distance rather than inequality.
        //
        // neutral is included rather than treated as "not a real category": it is what restauration
        // and createurs resolve to, so on screen it is a sixth chip, and a neutral that lands next
        // to water makes a food stand look like a water activity.
        listOf(
            LightCategoryColors to "light",
            DarkCategoryColors to "dark",
        ).forEach { (colors, theme) ->
            val all = colors.all()

            all.forEachIndexed { index, (nameA, colorA) ->
                all.drop(index + 1).forEach { (nameB, colorB) ->
                    val distance = perceptualDistance(colorA.fill, colorB.fill)

                    assertTrue(
                        distance >= MINIMUM_SEPARATION,
                        "$theme: $nameA and $nameB are ${distance.format()} apart, below $MINIMUM_SEPARATION",
                    )
                }
            }
        }
    }

    @Test
    fun everyCategory_carriesAnInkThatCanBeReadOnIt() {
        // 3:1 rather than 4.5:1, and the gap is the point. A filled Category surface is the fiche's
        // collapsing toolbar carrying a large title, which is what WCAG's large-text threshold
        // covers. No ink gets the identity's musique magenta past 4.5:1 — white reaches 4.2:1 and
        // dark ink only 3.8:1 — so a 4.5 floor here would fail the build over a colour SPEC.md
        // fixes. Small text never lands on a Category fill: the written Category label sits on the
        // page ground, above the title, which is also what keeps colour from being the only carrier.
        listOf(
            LightCategoryColors to "light",
            DarkCategoryColors to "dark",
        ).forEach { (colors, theme) ->
            colors.all().forEach { (name, category) ->
                val ratio = contrastRatio(category.ink, category.fill)

                assertTrue(
                    ratio >= WCAG_AA_LARGE_TEXT,
                    "$theme: $name ink is ${ratio.format()}:1 on its own fill, below $WCAG_AA_LARGE_TEXT:1",
                )
            }
        }
    }

    @Test
    fun everyCategory_isVisibleOnTheDarkGround() {
        // Only dark carries a contrast floor against the ground, and that asymmetry is deliberate
        // rather than an oversight. In light, the identity's own children yellow (#F5B000) is 1.8:1
        // on a near-white background — inherent to that hue, not to how the ramp was built.
        DarkCategoryColors.all().forEach { (name, category) ->
            val ratio = contrastRatio(category.fill, DarkAppColors.background)

            assertTrue(
                ratio >= WCAG_AA_NORMAL_TEXT,
                "dark: $name is ${ratio.format()}:1 on the background, below $WCAG_AA_NORMAL_TEXT:1",
            )
        }
    }

    @Test
    fun darkCategories_areALiftedSet_notTheLightOneReused() {
        // The light values sit too close to the dark ground to stay legible, so dark is lifted. If
        // someone collapses the two sets into one the test above would still pass, and the failure
        // would only show up on a phone in a tent at 01:00.
        LightCategoryColors.all().zip(DarkCategoryColors.all()).forEach { (light, dark) ->
            assertNotEquals(light.second.fill, dark.second.fill, "${light.first} is the same in both themes")
        }
    }

    @Test
    fun forId_measuredCategories_resolveToTheirOwnColour() {
        // The ids are French because the content is French. The properties are English because the
        // codebase is — CONTEXT.md draws that line, and this is where the two meet.
        with(LightCategoryColors) {
            assertEquals(music, forId("musique"))
            assertEquals(water, forId("eau"))
            assertEquals(land, forId("terre"))
            assertEquals(children, forId("enfants"))
            assertEquals(silent, forId("silent"))
        }
    }

    @Test
    fun forId_categoriesWithoutAMeasuredColour_resolveToNeutral() {
        // restauration and createurs exist in the 2026 content but were added after the identity was
        // fixed, so they have no measured hue. Neutral is deliberate, not a missing case.
        with(LightCategoryColors) {
            assertEquals(neutral, forId("restauration"))
            assertEquals(neutral, forId("createurs"))
        }
    }

    @Test
    fun forId_unknownCategory_resolvesToNeutralRatherThanFailing() {
        // The content may add a Category before the app has a colour for it. A neutral chip beats a
        // crash, and beats defaulting onto a hue that already means something else.
        assertEquals(LightCategoryColors.neutral, LightCategoryColors.forId("kayak"))
    }

    private fun CategoryColors.all() =
        listOf(
            "music" to music,
            "water" to water,
            "land" to land,
            "children" to children,
            "silent" to silent,
            "neutral" to neutral,
        )

    private companion object {
        // The six are currently no closer than 17.2 in light and 18.3 in dark. The floor sits below
        // both so a deliberate future adjustment is not blocked, but far enough above zero that a
        // collapse towards two indistinguishable chips fails.
        const val MINIMUM_SEPARATION = 12.0

        // WCAG 2.1 AA for text below 18pt.
        const val WCAG_AA_NORMAL_TEXT = 4.5

        // WCAG 2.1 AA for text at 18pt, or 14pt bold — the size a fiche's collapsing toolbar title
        // is set at, which is the only text that lands on a Category fill.
        const val WCAG_AA_LARGE_TEXT = 3.0
    }
}
