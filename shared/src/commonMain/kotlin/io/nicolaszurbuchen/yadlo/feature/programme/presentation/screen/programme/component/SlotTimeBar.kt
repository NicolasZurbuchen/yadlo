package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component

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
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.SlotLiveStateUiModel

/**
 * Where the Slot sits on the day, drawn on every row — before, during and after.
 *
 * This is the half of layout B2 that the pill cannot do. The pill answers "what about this one";
 * the bar answers "what is the shape of the day" — what overlaps what, how much of the afternoon a
 * seven-hour activity covers, whether the evening is empty. A finished row keeps it for exactly
 * that reason: by 21:00 on the Saturday most of the list is past, and a list of bare rows is a day
 * you can no longer read.
 *
 * While the Slot is running the segment lifts off the track and fills as it goes, so how far
 * through it is can be seen without reading the number that says the same thing.
 */
@Composable
fun SlotTimeBar(
    barStart: Float,
    barEnd: Float,
    categoryFill: Color,
    state: SlotLiveStateUiModel,
    modifier: Modifier = Modifier,
) {
    val progress =
        when (state) {
            is SlotLiveStateUiModel.Running -> state.progress
            is SlotLiveStateUiModel.Ending -> state.progress
            else -> null
        }

    // Weights must be positive, so a Slot flush against either end of the day contributes no
    // spacer rather than a zero-weight one, and a zero-length segment still draws a hairline.
    val lead = barStart.coerceIn(0f, 1f)
    val width = (barEnd - barStart).coerceIn(MINIMUM_SEGMENT, 1f)
    val trail = (1f - lead - width).coerceAtLeast(0f)

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
                            // The Category colour at low alpha over whatever ground the row sits
                            // on, so the unfilled remainder still reads as this Slot's segment
                            // rather than as track.
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
