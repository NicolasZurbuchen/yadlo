package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.design.theme.ShimmerPulse
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.shimmerBlock
import io.nicolaszurbuchen.yadlo.design.theme.spacing

/**
 * What the tab looks like while the bundle is still landing.
 *
 * **The shape of the answer, not a spinner in the middle of it** — the argument is made once, on
 * [io.nicolaszurbuchen.yadlo.design.theme.ShimmerPulse], and this is the screen it was made for.
 * What is local to here is the geometry: cards of rows, the first one long, a header above each.
 *
 * The counts are deliberately the published shape of the tab rather than a round number, so the
 * skeleton does not visibly resize the moment the content lands.
 */
@Composable
fun PlusSkeleton(modifier: Modifier = Modifier) {
    ShimmerPulse {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
            modifier = modifier.fillMaxWidth(),
        ) {
            GROUP_ROW_COUNTS.forEach { rowCount ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    // The group header, inset from the card exactly as PlusCard insets the real one.
                    Spacer(
                        modifier =
                            Modifier
                                .padding(horizontal = MaterialTheme.spacing.sm)
                                .width(HEADER_WIDTH)
                                .height(HEADER_HEIGHT)
                                .shimmerBlock(),
                    )

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.appColors.surface),
                    ) {
                        repeat(rowCount) { index ->
                            if (index > 0) {
                                HorizontalDivider(color = MaterialTheme.appColors.borderSubtle)
                            }

                            SkeletonRow()
                        }
                    }
                }
            }
        }
    }
}

/** The real row's geometry with its words taken out — same height, same icon slot, same insets. */
@Composable
private fun SkeletonRow(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = ROW_MIN_HEIGHT)
                .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
    ) {
        Spacer(modifier = Modifier.size(LEADING_ICON_SIZE).shimmerBlock())

        Spacer(modifier = Modifier.width(LABEL_WIDTH).height(LABEL_HEIGHT).shimmerBlock())
    }
}

// Sur place is the long card and the eye lands on it first, so the placeholder has to be long too;
// the three below it are the published sizes of Le festival, S'impliquer and L'application.
private val GROUP_ROW_COUNTS = listOf(8, 3, 3, 3)

private val HEADER_WIDTH = 96.dp
private val HEADER_HEIGHT = 12.dp
private val LABEL_WIDTH = 160.dp
private val LABEL_HEIGHT = 16.dp

// Matched to PlusRow, which is the whole point of drawing this instead of a spinner.
private val LEADING_ICON_SIZE = 20.dp
private val ROW_MIN_HEIGHT = 64.dp
