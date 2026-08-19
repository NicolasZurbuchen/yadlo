package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import io.nicolaszurbuchen.yadlo.common.content.presentation.component.SlotStatePill
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotLiveStateUiModel
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
 * **A kept Slot is a card and an unkept one is not.** The two lines and the heart sit at the same
 * insets either way, so the ground appearing behind one of them is the only difference, and *kept*
 * is legible from across the section rather than from the state of one small icon. The date is
 * written out in full — *samedi 10 juillet* — because three days of a festival is exactly the number
 * at which a weekday alone stops being an answer.
 *
 * Carries the same live-state pill as the Programme row it was opened from, because a visitor who
 * tapped `en cours` must not arrive at a screen that has gone quiet about it. Past Slots dim and
 * stay: a three-day activity whose Friday is over still has a Saturday, and hiding the Friday takes
 * the shape of the run with it.
 *
 * **The whole row is the target**, not the heart drawn at the end of it — DECISIONS.md § The heart
 * is attached to what you are saving. People expect to tap the row, a date row has nowhere else to
 * navigate to, and one target means the row can name its own action for a screen reader instead of
 * announcing a heart that says nothing about which day it belongs to. The disc around the heart is
 * drawn here rather than inside [SavedHeart] for the same reason it is drawn at all: it is the size
 * of a tap target on a row that is one, and the same mark in the toolbar has a bar around it
 * already.
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
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(if (slot.planned) MaterialTheme.appColors.surfaceRaised else Color.Transparent)
                .clickable(onClickLabel = action) { onClick(slot.id) }
                .alpha(if (isOver) PAST_ALPHA else 1f)
                .padding(MaterialTheme.spacing.sm),
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

        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .size(HEART_TARGET_SIZE)
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
                contentDescription = null,
                tint = if (slot.planned) MaterialTheme.appColors.onAccent else MaterialTheme.appColors.textTertiary,
                modifier = Modifier.size(HEART_SIZE),
            )
        }
    }
}

/** The Programme's value, so a Slot that has dimmed on the list is as dim on the fiche. */
private const val PAST_ALPHA = 0.45f

/** The disc the heart sits in — the minimum comfortable touch target, which the row's height follows. */
private val HEART_TARGET_SIZE = 40.dp

/** Smaller than a toolbar icon: this one sits beside body text rather than in a bar of its own. */
private val HEART_SIZE = 20.dp

/** One hairline. Any heavier and the empty disc competes with the filled one for attention. */
private val HEART_RING_WIDTH = 1.dp
