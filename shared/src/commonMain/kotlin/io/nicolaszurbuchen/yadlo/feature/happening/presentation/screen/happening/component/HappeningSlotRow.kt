package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.core.content.presentation.component.SlotStatePill
import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.HappeningSlotUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.asString
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.happening_slot_date
import yadlo.shared.generated.resources.plan_add
import yadlo.shared.generated.resources.plan_remove

/**
 * When this Happening happens — one row per Slot, the date on the first line and the time on the
 * second.
 *
 * The date is written out in full — *samedi 10 juillet* — because three days of a festival is
 * exactly the number at which a weekday alone stops being an answer.
 *
 * Carries the same live-state pill as the Programme row it was opened from, because a visitor who
 * tapped `en cours` must not arrive at a screen that has gone quiet about it. Past Slots dim and
 * stay: a three-day activity whose Friday is over still has a Saturday, and hiding the Friday takes
 * the shape of the run with it.
 *
 * **The heart is the target, and the row is not.** DECISIONS.md § The heart is attached to what you
 * are saving argued the opposite and this is a deliberate reversal of it: a whole tappable row wants
 * to look tappable, and the ground that made it look tappable was a card that stayed lit under a
 * kept Slot. That card was the thing being read wrong — a row that is *kept* is a fact about the
 * heart at its end, and painting the whole line to say so made it look selected, as though the fiche
 * had a current row. What is left is a date, a time, and one control that does something, which is
 * also what puts the text back on the same left edge as every other line on the screen.
 *
 * The mark keeps its disc: filled with the accent when kept, a ring when not, at the size of a thing
 * that is now genuinely pressed rather than a badge on something else that was.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HappeningSlotRow(
    slot: HappeningSlotUiModel,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isOver = slot.state is SlotLiveStateUiModel.Over
    val action = stringResource(if (slot.planned) Res.string.plan_remove else Res.string.plan_add)
    val dateText =
        stringResource(
            Res.string.happening_slot_date,
            slot.dayName,
            slot.dayNumber,
            slot.monthName.asString(),
        )

    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth().alpha(if (isOver) PAST_ALPHA else 1f),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = dateText.uppercase(),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.appColors.textTertiary,
            )

            // A FlowRow rather than a Row: at the largest text sizes `21:00 – 02:00` and
            // `se termine · 12 min` do not fit side by side, and the pill dropping under the time
            // it qualifies is the right way for that to fail.
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
                itemVerticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = slot.timeText,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.appColors.textPrimary,
                )

                slot.stateLabel?.let { label ->
                    SlotStatePill(label = label, state = slot.state)
                }
            }
        }

        IconButton(onClick = { onClick(slot.id) }) {
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier
                        .size(HEART_DISC_SIZE)
                        .clip(CircleShape)
                        .background(if (slot.planned) MaterialTheme.appColors.accent else Color.Transparent)
                        .then(
                            // A ring only when the disc is not filled, so an unkept Slot still shows
                            // where the mark is instead of leaving the heart floating on the page.
                            if (slot.planned) {
                                Modifier
                            } else {
                                Modifier.border(HEART_RING_WIDTH, MaterialTheme.appColors.borderStrong, CircleShape)
                            },
                        ),
            ) {
                SavedHeart(
                    isSaved = slot.planned,
                    // Named here because this is now the only thing on the row a screen reader can
                    // press, where before the row said it and the mark inside it stayed silent.
                    contentDescription = action,
                    tint = if (slot.planned) MaterialTheme.appColors.onAccent else MaterialTheme.appColors.textTertiary,
                    modifier = Modifier.size(HEART_SIZE),
                )
            }
        }
    }
}

/** The Programme's value, so a Slot that has dimmed on the list is as dim on the fiche. */
private const val PAST_ALPHA = 0.45f

/** The disc the heart sits in. The button around it is what carries the touch target. */
private val HEART_DISC_SIZE = 40.dp

/** Smaller than a toolbar icon: this one sits beside body text rather than in a bar of its own. */
private val HEART_SIZE = 20.dp

/** One hairline. Any heavier and the empty disc competes with the filled one for attention. */
private val HEART_RING_WIDTH = 1.dp
