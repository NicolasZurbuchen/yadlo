package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.design.theme.ShimmerPulse
import io.nicolaszurbuchen.yadlo.design.theme.shimmerBlock
import io.nicolaszurbuchen.yadlo.design.theme.spacing

/**
 * A fiche while its Happening is being read out of the content.
 *
 * *Reversed: this was a centred spinner.* This is the screen a spinner suited least: the fiche opens
 * on a photograph running to the top of the window under a transparent bar, and a rotating circle in
 * the middle of a white page is the opposite picture in every respect.
 *
 * **The photograph block is square-cornered and runs to the edges**, because the real one does — it
 * is the only image in the app that is not inset, and rounding or padding it here would put the
 * title in the wrong place for the frame it is about to sit in.
 *
 * What follows is the shape all three kinds share: the Category over the name, a row of tags, a
 * paragraph, then one section of rows. A fiche's later sections depend on what the content carries —
 * a price, a menu, links — and guessing at those would draw a screen most Happenings do not have.
 */
@Composable
fun HappeningSkeleton(modifier: Modifier = Modifier) {
    ShimmerPulse {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
            modifier = modifier.fillMaxWidth(),
        ) {
            // The photograph, with the Category label and the title anchored to its bottom edge.
            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(HEADER_HEIGHT)
                        .shimmerBlock(RoundedCornerShape(NO_CORNER)),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
                modifier = Modifier.fillMaxWidth().padding(horizontal = MaterialTheme.spacing.md),
            ) {
                // The tags: genres for an Artist, the offering for a Stand.
                Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
                    TAG_WIDTHS.forEach { width ->
                        Spacer(
                            modifier =
                                Modifier
                                    .width(width.dp)
                                    .height(TAG_HEIGHT)
                                    .shimmerBlock(RoundedCornerShape(TAG_HEIGHT / 2)),
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
                    repeat(DESCRIPTION_LINES) {
                        Spacer(modifier = Modifier.fillMaxWidth().height(BODY_HEIGHT).shimmerBlock())
                    }

                    // The last line of a paragraph is the one that stops short.
                    Spacer(modifier = Modifier.fillMaxWidth(BODY_LAST_LINE).height(BODY_HEIGHT).shimmerBlock())
                }

                Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)) {
                    // *Quand* — the section every Happening with hours carries.
                    Spacer(modifier = Modifier.width(SECTION_TITLE_WIDTH).height(SECTION_TITLE_HEIGHT).shimmerBlock())

                    repeat(SLOT_ROWS) {
                        Spacer(modifier = Modifier.fillMaxWidth().height(SLOT_ROW_HEIGHT).shimmerBlock())
                    }
                }
            }
        }
    }
}

/**
 * The header's own height. It is a fixed band rather than an aspect ratio because the fiche's
 * photograph is cropped to it, and a taller placeholder would push the title off the first screen.
 */
private val HEADER_HEIGHT = 280.dp

/** Square, because the photograph is: it is the one image in the app that runs edge to edge. */
private val NO_CORNER = 0.dp

private val TAG_WIDTHS = listOf(72, 96, 64)
private val TAG_HEIGHT = 26.dp

private const val DESCRIPTION_LINES = 3
private const val BODY_LAST_LINE = 0.55f
private val BODY_HEIGHT = 14.dp

private val SECTION_TITLE_WIDTH = 84.dp
private val SECTION_TITLE_HEIGHT = 12.dp

/** Two dates, which is the common case: a weekend activity runs on more than one day. */
private const val SLOT_ROWS = 2
private val SLOT_ROW_HEIGHT = 44.dp
