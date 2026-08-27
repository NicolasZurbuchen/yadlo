package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.announcements.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.design.theme.ShimmerPulse
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.shimmerBlock
import io.nicolaszurbuchen.yadlo.design.theme.spacing

/**
 * The feed while it is arriving — a date, a title, two lines of body, a rule, repeated.
 *
 * *Reversed: this was a centred spinner.* A spinner is the same picture on every screen in the app
 * and says only that something is happening; this says what is about to arrive and in what shape,
 * so the annonces land into a layout the eye has already settled on rather than replacing a
 * rotating circle.
 *
 * **The rows are deliberately uneven.** Every title the same width reads as a loading graphic
 * rather than as text about to appear, and annonce titles run from four words to a full line. The
 * fractions below are taken from the eleven the 2026 file publishes.
 *
 * One [ShimmerPulse] around the whole column, so every block breathes off a single animated value
 * instead of eight independently-phased ones.
 */
@Composable
fun AnnouncementsSkeleton(modifier: Modifier = Modifier) {
    ShimmerPulse {
        Column(modifier = modifier.fillMaxWidth()) {
            TITLE_WIDTHS.forEach { titleWidth ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = MaterialTheme.spacing.md,
                                vertical = MaterialTheme.spacing.md,
                            ),
                ) {
                    Spacer(modifier = Modifier.width(DATE_WIDTH).height(DATE_HEIGHT).shimmerBlock())

                    Spacer(modifier = Modifier.fillMaxWidth(titleWidth).height(TITLE_HEIGHT).shimmerBlock())

                    Spacer(modifier = Modifier.fillMaxWidth().height(BODY_HEIGHT).shimmerBlock())

                    Spacer(modifier = Modifier.fillMaxWidth(BODY_LAST_LINE).height(BODY_HEIGHT).shimmerBlock())
                }

                // The rule belongs to the skeleton too: it is the only thing separating one annonce
                // from the next, so a placeholder without it is a wall rather than a list.
                HorizontalDivider(
                    color = MaterialTheme.appColors.borderSubtle,
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                )
            }
        }
    }
}

/**
 * Six rows, which fills a phone without the list visibly growing when the real ones land. The
 * fractions are title lengths from the published feed rather than a decreasing ramp, which is what
 * keeps this reading as text.
 */
private val TITLE_WIDTHS = listOf(0.72f, 0.55f, 0.88f, 0.61f, 0.79f, 0.5f)

/** The last line of a paragraph is the one that stops short; the rest run the full width. */
private const val BODY_LAST_LINE = 0.42f

private val DATE_WIDTH = 72.dp
private val DATE_HEIGHT = 12.dp
private val TITLE_HEIGHT = 18.dp
private val BODY_HEIGHT = 14.dp
