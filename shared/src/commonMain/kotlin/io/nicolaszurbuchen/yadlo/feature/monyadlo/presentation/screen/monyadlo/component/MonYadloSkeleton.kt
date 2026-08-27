package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.navigation.LocalTabChromeInsets
import io.nicolaszurbuchen.yadlo.design.theme.ShimmerPulse
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.shimmerBlock
import io.nicolaszurbuchen.yadlo.design.theme.spacing

/**
 * Mon Yadlo while the Plan and the content are being read together.
 *
 * *Reversed: this was a centred spinner.* What is drawn is the shape the tab has whether or not
 * anything is saved: the axis on its blue band, the *À essayer* tile, then one day of the timeline.
 *
 * **The rail is drawn, and it is the piece that matters.** A day on this screen is a date column on
 * the left with its rows hung beside it, and that left edge is what makes the list read as a
 * timeline rather than as cards. A silhouette without it would settle the eye on the wrong layout.
 *
 * One day rather than three: an empty Plan is the ordinary first launch, and drawing a full weekend
 * that then collapses to a single empty message is a worse guess than drawing less than arrives.
 */
@Composable
fun MonYadloSkeleton(modifier: Modifier = Modifier) {
    val chrome = LocalTabChromeInsets.current

    ShimmerPulse {
        Column(modifier = modifier.fillMaxSize().padding(top = chrome.top)) {
            // The axis, on the same band the real one sits on, inset to where the bars begin.
            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.appColors.primarySubtle)
                        .padding(
                            start = MaterialTheme.spacing.md + RAIL_WIDTH + MaterialTheme.spacing.md,
                            end = MaterialTheme.spacing.sm + CHEVRON_SIZE + MaterialTheme.spacing.sm,
                            top = MaterialTheme.spacing.sm,
                            bottom = MaterialTheme.spacing.sm + MaterialTheme.spacing.xs,
                        )
                        .height(SCALE_HEIGHT)
                        .shimmerBlock(),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            start = MaterialTheme.spacing.md,
                            end = MaterialTheme.spacing.md,
                            top = MaterialTheme.spacing.md,
                        ),
            ) {
                // *À essayer*, which is a destination rather than a row and keeps its own height.
                Spacer(modifier = Modifier.fillMaxWidth().height(WISHLIST_TILE_HEIGHT).shimmerBlock())

                Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)) {
                    // The date column: a weekday over the day of the month.
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
                        modifier = Modifier.width(RAIL_WIDTH),
                    ) {
                        Spacer(modifier = Modifier.fillMaxWidth().height(WEEKDAY_HEIGHT).shimmerBlock())

                        Spacer(modifier = Modifier.fillMaxWidth().height(DAY_NUMBER_HEIGHT).shimmerBlock())
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        NAME_WIDTHS.forEach { nameWidth ->
                            PlannedRowSilhouette(nameWidth = nameWidth)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlannedRowSilhouette(nameWidth: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.size(CATEGORY_MARK_SIZE).shimmerBlock())

            Spacer(modifier = Modifier.fillMaxWidth(nameWidth).height(NAME_HEIGHT).shimmerBlock())
        }

        Spacer(modifier = Modifier.width(TIME_WIDTH).height(TIME_HEIGHT).shimmerBlock())

        Spacer(modifier = Modifier.fillMaxWidth(BAR_WIDTH).height(BAR_HEIGHT).shimmerBlock())
    }
}

/** Three saved Slots on one day, which is about what an evening on the Plan looks like. */
private val NAME_WIDTHS = listOf(0.62f, 0.44f, 0.71f)

// PlannedSlotRow's own mark, so the row does not shift sideways when it lands.
private val CATEGORY_MARK_SIZE = 10.dp

private val SCALE_HEIGHT = 10.dp
private val WISHLIST_TILE_HEIGHT = 72.dp
private val WEEKDAY_HEIGHT = 11.dp
private val DAY_NUMBER_HEIGHT = 22.dp
private val NAME_HEIGHT = 16.dp
private val TIME_HEIGHT = 12.dp
private val TIME_WIDTH = 96.dp
private const val BAR_WIDTH = 0.9f
private val BAR_HEIGHT = 6.dp
