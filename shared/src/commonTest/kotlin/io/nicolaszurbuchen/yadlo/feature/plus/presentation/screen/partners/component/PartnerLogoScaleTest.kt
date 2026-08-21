package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners.component

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The area rule behind the partners grid, checked against the shape of the real bank.
 *
 * The numbers here are the two boxes the screen actually draws — a three-column cell of 88 by 56
 * points inside its padding, and a two-column one of 144 by 80 — and the aspect ratios are measured
 * from the files: 0.83 for Volt-A, which is the tallest logo the association has supplied, up to
 * 6.38 for VSM, which is the widest.
 */
class PartnerLogoScaleTest {
    @Test
    fun logoScaleFor_aSquareLogoAndAWideOne_endUpCoveringTheSameArea() {
        // The whole point of the rule. Under plain Fit these two differ by a factor of nearly three:
        // the square fills the cell's height and the 3:1 fills its width, on one twelfth of it.
        val square = areaOf(ratio = 1f, box = THREE_COLUMN)
        val wide = areaOf(ratio = 3f, box = THREE_COLUMN)

        assertTrue(
            abs(square - wide) / square < TOLERANCE,
            "a square logo covers $square and a 3:1 logo $wide",
        )
    }

    @Test
    fun logoScaleFor_theTallestAndTheWidestTheRuleCanReach_joinTheSameSet() {
        val volta = areaOf(ratio = 0.83f, box = THREE_COLUMN)
        val target = areaOf(ratio = 1f, box = THREE_COLUMN)

        assertTrue(abs(volta - target) / target < TOLERANCE, "Volt-A covers $volta against $target")
    }

    @Test
    fun logoScaleFor_aLogoWiderThanTheRuleCanReach_isDrawnExactlyAsFitWould() {
        // VSM is six times wider than tall. Reaching the target area would mean spilling out of the
        // cell, so it is capped and stays lighter than the rest — the acknowledged limit of this.
        assertEquals(1f, logoScaleFor(aspectRatio = 6.38f, boxAspectRatio = THREE_COLUMN))
    }

    @Test
    fun logoScaleFor_everyRatioInTheBank_onlyEverShrinks() {
        // Never above 1, in either cell. A factor above 1 would draw a logo outside the card that is
        // meant to contain it, which is the one failure mode that would be visible as a bug.
        for (box in listOf(THREE_COLUMN, TWO_COLUMN)) {
            for (ratio in BANK_RATIOS) {
                val scale = logoScaleFor(aspectRatio = ratio, boxAspectRatio = box)

                assertTrue(scale in 0f..1f, "ratio $ratio in a $box box scaled by $scale")
            }
        }
    }

    @Test
    fun logoScaleFor_theWiderCellOfAProminentTier_drawsTheSameLogoLarger() {
        // Two columns is the top three tiers' reward, and it has to actually pay: the same logo must
        // come out with more ink on a Sponsor général's card than on a Partenaire's.
        val prominent = 144f * 80f * areaOf(ratio = 2f, box = TWO_COLUMN)
        val ordinary = 88f * 56f * areaOf(ratio = 2f, box = THREE_COLUMN)

        assertTrue(prominent > ordinary, "prominent $prominent against ordinary $ordinary")
    }

    @Test
    fun logoScaleFor_aRatioThatCannotBeMeasured_isLeftAlone() {
        // Before the bytes land there is no intrinsic size, and a zero would otherwise divide.
        assertEquals(1f, logoScaleFor(aspectRatio = 0f, boxAspectRatio = THREE_COLUMN))
        assertEquals(1f, logoScaleFor(aspectRatio = 2f, boxAspectRatio = 0f))
    }

    /**
     * What a logo of [ratio] ends up covering, as a fraction of a box normalised to height 1 — the
     * same normalisation [logoScaleFor] works in, so the two can be compared without a screen.
     */
    private fun areaOf(
        ratio: Float,
        box: Float,
    ): Float {
        val fitArea = if (ratio >= box) box * box / ratio else ratio

        val scale = logoScaleFor(aspectRatio = ratio, boxAspectRatio = box)

        return fitArea * scale * scale
    }

    private companion object {
        /** 88 by 56 points inside the padding of a three-across cell. */
        const val THREE_COLUMN = 88f / 56f

        /** And 144 by 80 in a two-across one. */
        const val TWO_COLUMN = 144f / 80f

        /** Measured from the thirty-nine files, from the tallest to the widest. */
        val BANK_RATIOS = listOf(0.83f, 1f, 1.41f, 1.75f, 2.24f, 3.49f, 4.69f, 5.22f, 6.38f)

        /** Floating-point slack. The rule is a design heuristic, not an accounting identity. */
        const val TOLERANCE = 0.01f
    }
}
