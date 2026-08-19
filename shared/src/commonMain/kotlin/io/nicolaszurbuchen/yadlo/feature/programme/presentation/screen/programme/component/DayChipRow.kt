package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.component.YadloFilterChip
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.DayChipUiModel

/**
 * Vendredi · Samedi · Dimanche — the day the list is showing.
 *
 * The names come from the content rather than from the dates, so a day the association calls
 * something else keeps its name, and nothing here has to translate a weekday.
 *
 * Scrollable because three days is this edition's number, not the app's: the row must not clip if a
 * fourth is ever added or if the text size doubles.
 *
 * [YadloFilterChip] rather than a `Text` on a rounded background, which is what this used to be: it
 * sat a few dp taller than the Category chips directly beneath it, which is the sort of difference
 * nobody can name and everybody sees.
 *
 * The chip brings the page ground with it and draws its edge in the ink the blue carries — see
 * [ProgrammeHeader] for why a control on this ground cannot simply inherit the page's roles.
 */
@Composable
fun DayChipRow(
    days: List<DayChipUiModel>,
    onDayClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier =
            modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(start = MaterialTheme.spacing.md, end = MaterialTheme.spacing.md, bottom = MaterialTheme.spacing.xs),
    ) {
        days.forEach { day ->
            YadloFilterChip(
                label = day.name,
                isSelected = day.isSelected,
                onClick = { onDayClick(day.id) },
                container = MaterialTheme.appColors.primarySubtle,
                outline = MaterialTheme.appColors.onPrimarySubtle,
            )
        }
    }
}
