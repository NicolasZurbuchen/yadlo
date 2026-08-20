package io.nicolaszurbuchen.yadlo.common.content.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotScaleUiModel

/**
 * The span, written once above a list of bars — the axis every segment under it is drawn against.
 *
 * **It carries no inset of its own, and that is the whole contract.** A scale offset from the axis it
 * labels is worse than no scale: it does not fail to answer the question, it answers it wrongly. Only
 * the screen knows where its bars actually begin — the Programme's start at the row's left edge and
 * stop where the chevron column starts; Mon Yadlo's start 96dp in, past a 64dp date rail and the gap
 * after it, because the rail takes the left of that screen. So the caller pads this, and each one
 * pads it with the same numbers its rows are built from.
 *
 * The ink is the one the chrome blue carries rather than the dim metadata role these three would
 * take on the page: there is no dim step that clears 4.5:1 on that ground — the tertiary role
 * measures 2.4:1 — and three times nobody can read is worse than three times that are not quieter
 * than the chips.
 */
@Composable
fun SlotScaleRow(
    scale: SlotScaleUiModel,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth(),
    ) {
        listOf(scale.startText, scale.middleText, scale.endText).forEach { reading ->
            Text(
                text = reading,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.appColors.onPrimarySubtle,
            )
        }
    }
}
