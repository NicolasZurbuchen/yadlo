package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.ProgrammeScaleUiModel

/**
 * The day's span, written once above the list — the axis every row's bar is drawn against.
 *
 * Three readings rather than an hourly ruler: the bar is for the shape of the day, not for reading
 * a time off, and the exact hours are on the row itself. Once at the top rather than per row is
 * what keeps this from becoming the right-hand time column layout B2 exists to avoid.
 *
 * The ink the chrome blue carries rather than the dim metadata role these three would take on the
 * page: there is no dim step that clears 4.5:1 on that ground — the tertiary role measures 2.4:1 —
 * and three times nobody can read is worse than three times that are not quieter than the chips.
 *
 * It carries [SlotRow]'s own insets — the Category mark on the left, the chevron on the right — so
 * the readings sit over the positions they describe. A scale offset from the axis it labels is worse
 * than no scale: it does not fail to answer the question, it answers it wrongly.
 */
@Composable
fun ProgrammeScaleRow(
    scale: ProgrammeScaleUiModel,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    PaddingValues(
                        start = MaterialTheme.spacing.md + CATEGORY_MARK_SIZE + MaterialTheme.spacing.sm,
                        top = MaterialTheme.spacing.xs,
                        end = MaterialTheme.spacing.md + CHEVRON_SIZE + MaterialTheme.spacing.sm,
                        bottom = MaterialTheme.spacing.xs,
                    ),
                ),
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
