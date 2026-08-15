package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.common.content.presentation.component.SlotStatePill
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotLiveStateUiModel
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.HappeningSlotUiModel

/**
 * When this Happening happens — one row per Slot, the day and the time written once as a range.
 *
 * Carries the same live-state pill as the Programme row it was opened from, because a visitor who
 * tapped `en cours` must not arrive at a screen that has gone quiet about it. Past Slots dim and
 * stay: a three-day activity whose Friday is over still has a Saturday, and hiding the Friday takes
 * the shape of the run with it.
 *
 * This is the row the heart attaches to — DECISIONS.md § The heart is attached to what you are
 * saving. It is not clickable yet because there is nothing to save into.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HappeningSlotRow(
    slot: HappeningSlotUiModel,
    modifier: Modifier = Modifier,
) {
    val isOver = slot.state is SlotLiveStateUiModel.Over

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
        itemVerticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .alpha(if (isOver) PAST_ALPHA else 1f)
                .padding(vertical = MaterialTheme.spacing.xs),
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
}

/** The Programme's value, so a Slot that has dimmed on the list is as dim on the fiche. */
private const val PAST_ALPHA = 0.45f
