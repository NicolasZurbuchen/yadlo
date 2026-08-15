package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.common.content.presentation.component.SlotStatePill
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.HappeningSlotUiModel
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.plan_add
import yadlo.shared.generated.resources.plan_remove

/**
 * When this Happening happens — one row per Slot, the day and the time written once as a range.
 *
 * Carries the same live-state pill as the Programme row it was opened from, because a visitor who
 * tapped `en cours` must not arrive at a screen that has gone quiet about it. Past Slots dim and
 * stay: a three-day activity whose Friday is over still has a Saturday, and hiding the Friday takes
 * the shape of the run with it.
 *
 * **The whole row is the target**, not the heart drawn at the end of it — DECISIONS.md § The heart
 * is attached to what you are saving. People expect to tap the row, a date row has nowhere else to
 * navigate to, and one target means the row can name its own action for a screen reader instead of
 * announcing a heart that says nothing about which day it belongs to.
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

    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClickLabel = action) { onClick(slot.id) }
                .alpha(if (isOver) PAST_ALPHA else 1f)
                .padding(vertical = MaterialTheme.spacing.xs),
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            itemVerticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = slot.dayName,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.appColors.textPrimary,
            )

            Text(
                text = slot.timeText,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.appColors.textSecondary,
            )

            slot.stateLabel?.let { label ->
                SlotStatePill(label = label, state = slot.state)
            }
        }

        SavedHeart(
            isSaved = slot.planned,
            contentDescription = null,
            tint = if (slot.planned) MaterialTheme.appColors.accent else MaterialTheme.appColors.textTertiary,
            modifier = Modifier.size(HEART_SIZE),
        )
    }
}

/** The Programme's value, so a Slot that has dimmed on the list is as dim on the fiche. */
private const val PAST_ALPHA = 0.45f

/** Smaller than a toolbar icon: this one sits beside body text rather than in a bar of its own. */
private val HEART_SIZE = 20.dp
