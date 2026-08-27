package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.design.theme.shimmerBlock
import io.nicolaszurbuchen.yadlo.design.theme.spacing

/**
 * *L'histoire* while it is still arriving: two paragraphs, a titled passage, and the figure grid.
 *
 * Its own rather than the default page skeleton because of the last block. The figures are the one
 * thing on this screen that is not prose, they are the part a reader scrolls to, and a placeholder
 * that drew three more lines of text there would settle the eye on a layout that then rearranges
 * itself.
 */
@Composable
fun StorySkeleton(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
        modifier = modifier.fillMaxWidth(),
    ) {
        SkeletonLines(widths = BODY_LINES)

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Spacer(modifier = Modifier.width(HEADER_WIDTH).height(HEADER_HEIGHT).shimmerBlock())

            SkeletonLines(widths = PASSAGE_LINES)
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Spacer(modifier = Modifier.width(HEADER_WIDTH).height(HEADER_HEIGHT).shimmerBlock())

            // Two rows of two, which is the shape three figures take in YadloFigureGrid — the odd one
            // keeps its column and the empty cell beside it is drawn as nothing, exactly as there.
            repeat(FIGURE_ROWS) { rowIndex ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    val cells = if (rowIndex == FIGURE_ROWS - 1) 1 else FIGURE_COLUMNS

                    repeat(cells) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
                            modifier = Modifier.weight(1f),
                        ) {
                            Spacer(modifier = Modifier.width(FIGURE_WIDTH).height(FIGURE_HEIGHT).shimmerBlock())

                            Spacer(modifier = Modifier.width(LABEL_WIDTH).height(LINE_HEIGHT).shimmerBlock())
                        }
                    }

                    repeat(FIGURE_COLUMNS - cells) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun SkeletonLines(
    widths: List<Float>,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier = modifier.fillMaxWidth(),
    ) {
        widths.forEach { fraction ->
            Spacer(modifier = Modifier.fillMaxWidth(fraction).height(LINE_HEIGHT).shimmerBlock())
        }
    }
}

// The origin runs to two paragraphs on the published page; the passage is shorter.
private val BODY_LINES = listOf(1f, 1f, 1f, 0.55f)
private val PASSAGE_LINES = listOf(1f, 0.7f)

// Three figures, two to a row.
private const val FIGURE_COLUMNS = 2
private const val FIGURE_ROWS = 2

private val HEADER_WIDTH = 96.dp
private val HEADER_HEIGHT = 12.dp
private val LINE_HEIGHT = 16.dp

// Sized to the display face the real number is set in, which is what makes the grid keep its height
// when the figures land.
private val FIGURE_WIDTH = 88.dp
private val FIGURE_HEIGHT = 36.dp
private val LABEL_WIDTH = 64.dp
