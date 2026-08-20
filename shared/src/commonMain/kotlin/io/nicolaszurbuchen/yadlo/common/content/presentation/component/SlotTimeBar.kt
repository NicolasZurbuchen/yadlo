package io.nicolaszurbuchen.yadlo.common.content.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotSegmentUiModel

/**
 * Where the row's Slots sit on the day, drawn on every row — before, during and after.
 *
 * This is the half of layout B2 that the pill cannot do. The pill answers "what about this one";
 * the bar answers "what is the shape of the day" — what overlaps what, how much of the afternoon a
 * seven-hour activity covers, whether the evening is empty. A finished row keeps it for exactly
 * that reason: by 21:00 on the Saturday most of the list is past, and a list of bare rows is a day
 * you can no longer read.
 *
 * While a Slot is running its segment lifts off the track and fills as it goes, so how far through
 * it is can be seen without reading the number that says the same thing.
 *
 * **[segments] is a list because a row is a Happening on a day, not a Slot.** SUP Yoga's Saturday
 * is three hours on one track, which is the clearest thing the merged row does: three marks spread
 * across the afternoon say "three chances at this" faster than any sentence, and the one that has
 * gone reads as gone while the two ahead do not. Each is drawn from its own state, so a row can
 * legitimately carry a finished segment and a raised one at the same time.
 *
 * They are laid out with weights against a cursor rather than positioned absolutely, so the gaps
 * between them are real layout rather than arithmetic on a width this composable does not know.
 *
 * It sits in `common/content` because Mon Yadlo draws the same segment against the same axis. It
 * moved up here for that second caller rather than in anticipation of it, the way the live-state
 * vocabulary and the pill did before it.
 */
@Composable
fun SlotTimeBar(
    segments: List<SlotSegmentUiModel>,
    categoryFill: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier.fillMaxWidth().height(BAR_HEIGHT),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(TRACK_HEIGHT)
                    .clip(TRACK_SHAPE)
                    .background(MaterialTheme.appColors.surfaceRaised),
        )

        Row(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
            // How far along the track the last segment ended. Weights must be positive, so a Slot
            // flush against the previous one contributes no spacer rather than a zero-weight one.
            var cursor = 0f

            segments.forEach { segment ->
                val progress =
                    when (segment.state) {
                        is SlotLiveStateUiModel.Running -> segment.state.progress
                        is SlotLiveStateUiModel.Ending -> segment.state.progress
                        else -> null
                    }

                val lead = (segment.barStart - cursor).coerceAtLeast(0f)
                val width = (segment.barEnd - segment.barStart).coerceIn(MINIMUM_SEGMENT, 1f)

                if (lead > 0f) {
                    Spacer(modifier = Modifier.weight(lead))
                }

                if (progress == null) {
                    Box(
                        modifier =
                            Modifier
                                .weight(width)
                                .align(Alignment.CenterVertically)
                                .height(TRACK_HEIGHT)
                                .clip(TRACK_SHAPE)
                                .background(categoryFill),
                    )
                } else {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier =
                            Modifier
                                .weight(width)
                                .align(Alignment.CenterVertically)
                                .height(BAR_HEIGHT)
                                .clip(RAISED_SHAPE)
                                // The Category colour at low alpha over whatever ground the row
                                // sits on, so the unfilled remainder still reads as this Slot's
                                // segment rather than as track.
                                .background(categoryFill.copy(alpha = RAISED_GROUND_ALPHA))
                                .border(RAISED_BORDER, categoryFill, RAISED_SHAPE),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth(progress)
                                    .fillMaxHeight()
                                    .background(categoryFill),
                        )
                    }
                }

                cursor = (cursor + lead + width).coerceAtMost(1f)
            }

            val trail = (1f - cursor).coerceAtLeast(0f)

            if (trail > 0f) {
                Spacer(modifier = Modifier.weight(trail))
            }
        }
    }
}

/** Tall enough to hold the raised segment, so a row does not change height when a Slot starts. */
private val BAR_HEIGHT = 14.dp

private val TRACK_HEIGHT = 6.dp

private val TRACK_SHAPE = RoundedCornerShape(3.dp)
private val RAISED_SHAPE = RoundedCornerShape(7.dp)
private val RAISED_BORDER = 1.5.dp

/** The prototype's 26%: enough to read as filled, light enough that the progress reads over it. */
private const val RAISED_GROUND_ALPHA = 0.26f

/**
 * A Slot short enough to round to nothing against a fifteen-hour day still has to be visible — a
 * one-hour set on the Sunday is 0.7% of the axis.
 */
private const val MINIMUM_SEGMENT = 0.012f
