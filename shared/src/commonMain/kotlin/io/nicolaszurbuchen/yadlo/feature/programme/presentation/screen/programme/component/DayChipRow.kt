package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
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
                .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
    ) {
        days.forEach { day ->
            Text(
                text = day.name,
                style = MaterialTheme.typography.titleSmall,
                color = if (day.isSelected) MaterialTheme.appColors.onPrimary else MaterialTheme.appColors.textSecondary,
                modifier =
                    Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(
                            if (day.isSelected) {
                                MaterialTheme.appColors.primary
                            } else {
                                MaterialTheme.appColors.surfaceRaised
                            },
                        )
                        .selectable(
                            selected = day.isSelected,
                            role = Role.Tab,
                            onClick = { onDayClick(day.id) },
                        )
                        .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
            )
        }
    }
}
