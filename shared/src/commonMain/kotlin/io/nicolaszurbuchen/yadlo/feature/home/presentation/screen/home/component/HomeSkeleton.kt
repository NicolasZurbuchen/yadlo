package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.design.theme.ShimmerPulse
import io.nicolaszurbuchen.yadlo.design.theme.shimmerBlock
import io.nicolaszurbuchen.yadlo.design.theme.spacing
import io.nicolaszurbuchen.yadlo.design.theme.tabContentPadding

/**
 * Accueil while the first bundle is still landing.
 *
 * *Reversed: this was a centred spinner.* It is a short-lived state — App.kt holds the splash until
 * the content is Ready — but "short-lived" is exactly the argument for a silhouette over a
 * spinner: a rotating circle that appears for 200ms is a flash of unrelated geometry, while a
 * shape that matches what follows reads as the screen arriving.
 *
 * **The stack drawn is the search field, a countdown and an annonce**, which is every phase's
 * opening except LIVE. There is no way to know the Phase before the content lands, so the skeleton
 * draws the shape four of the five share rather than guessing at the fifth.
 *
 * It takes the same [tabContentPadding] the real list does, so nothing shifts vertically when the
 * blocks replace it.
 */
@Composable
fun HomeSkeleton(modifier: Modifier = Modifier) {
    ShimmerPulse {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
            modifier =
                modifier
                    .fillMaxWidth()
                    .padding(
                        tabContentPadding(
                            start = MaterialTheme.spacing.md,
                            top = MaterialTheme.spacing.md,
                            end = MaterialTheme.spacing.md,
                            bottom = MaterialTheme.spacing.md,
                        ),
                    ),
        ) {
            // The search field, which is first in every stack.
            Spacer(modifier = Modifier.fillMaxWidth().height(SEARCH_HEIGHT).shimmerBlock())

            // The countdown: a large number over a line naming the edition and the venue.
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Spacer(modifier = Modifier.width(COUNTDOWN_WIDTH).height(COUNTDOWN_HEIGHT).shimmerBlock())

                Spacer(modifier = Modifier.fillMaxWidth(SUBTITLE_WIDTH).height(LINE_HEIGHT).shimmerBlock())
            }

            // One annonce under its section heading.
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Spacer(modifier = Modifier.width(HEADING_WIDTH).height(HEADING_HEIGHT).shimmerBlock())

                Spacer(modifier = Modifier.fillMaxWidth().height(CARD_HEIGHT).shimmerBlock())
            }
        }
    }
}

// Matched to YadloSearchField's own resting height, so the field does not jump when it arrives.
private val SEARCH_HEIGHT = 52.dp

// The `J-19` display figure and the line under it.
private val COUNTDOWN_WIDTH = 128.dp
private val COUNTDOWN_HEIGHT = 44.dp
private const val SUBTITLE_WIDTH = 0.6f

private val HEADING_WIDTH = 112.dp
private val HEADING_HEIGHT = 12.dp
private val LINE_HEIGHT = 14.dp

/** One annonce card, which is where the eye lands once the countdown is read. */
private val CARD_HEIGHT = 96.dp
