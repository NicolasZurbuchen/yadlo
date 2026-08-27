package io.nicolaszurbuchen.yadlo.feature.plus.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.design.theme.shimmerBlock
import io.nicolaszurbuchen.yadlo.design.theme.spacing

/**
 * What a Plus page looks like before its words arrive: a short paragraph, then two headed sections.
 *
 * The default for [PlusDetailScaffold], and the right answer for most of the tab — *À propos*,
 * *Confidentialité*, *Festival responsable* and the FAQ are all prose under headings, so a skeleton
 * that draws prose under headings is not a placeholder for them, it is their shape. The screens
 * whose shape is genuinely different — a day card, a hero, a directory of addresses — pass their own
 * rather than inheriting a lie about what is coming.
 *
 * Line widths taper the way a real paragraph does. A block of identical full-width bars reads as a
 * table, and the eye settles on a layout it then has to unlearn.
 */
@Composable
fun PlusPageSkeleton(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
        modifier = modifier.fillMaxWidth(),
    ) {
        SkeletonParagraph(lineWidths = INTRO_LINES)

        repeat(SECTION_COUNT) {
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Spacer(
                    modifier =
                        Modifier
                            .width(HEADER_WIDTH)
                            .height(HEADER_HEIGHT)
                            .shimmerBlock(),
                )

                SkeletonParagraph(lineWidths = SECTION_LINES)
            }
        }
    }
}

@Composable
private fun SkeletonParagraph(
    lineWidths: List<Float>,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier = modifier.fillMaxWidth(),
    ) {
        lineWidths.forEach { fraction ->
            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth(fraction)
                        .height(LINE_HEIGHT)
                        .shimmerBlock(),
            )
        }
    }
}

// Fractions rather than widths, so the taper survives a phone held sideways and the largest
// accessibility text size alike.
private val INTRO_LINES = listOf(1f, 1f, 0.62f)
private val SECTION_LINES = listOf(1f, 0.84f)

// Two, because a page with one section and a page with five both settle into this without the
// skeleton visibly resizing when the real thing lands.
private const val SECTION_COUNT = 2

// The heading is set in the display face at a smaller size than the body it introduces, which is
// what makes it read as a label rather than a title — the placeholder has to be shorter, not taller.
private val HEADER_WIDTH = 96.dp
private val HEADER_HEIGHT = 12.dp
private val LINE_HEIGHT = 16.dp
