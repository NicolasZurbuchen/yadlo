package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo.MonYadloDayUiModel

/**
 * One day of the Plan: the date on a rail down the left, its Slots scrolling past to the right.
 *
 * The rail is what the layout was chosen for — DECISIONS.md § Mon Yadlo layout measured rows with
 * sticky headers at 829px, this at 882px and cards at 964px. The 6% extra scrolling buys a date that
 * is visible the whole time you are reading the day it belongs to, which a header loses the moment
 * it scrolls off.
 *
 * The day name is written above the date because it is what someone thinks in — "le samedi" — and
 * the numeric date is what a poster and a bus timetable are written in.
 */
@Composable
fun PlannedDayBlock(
    day: MonYadloDayUiModel,
    onRowClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth().padding(horizontal = MaterialTheme.spacing.md)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            modifier = Modifier.width(RAIL_WIDTH).padding(top = MaterialTheme.spacing.sm),
        ) {
            Text(
                text = day.name.uppercase(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.appColors.textPrimary,
            )

            Text(
                text = day.dateText,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.appColors.textTertiary,
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            day.rows.forEachIndexed { index, row ->
                // A hairline between neighbours and never around one of them — the shared left edge
                // is why this is a list of rows rather than a stack of cards.
                if (index > 0) {
                    HorizontalDivider(color = MaterialTheme.appColors.borderSubtle)
                }

                PlannedSlotRow(row = row, onClick = onRowClick)
            }
        }
    }
}

/**
 * Wide enough for `VENDREDI` in the condensed display face at 13sp, which is the longest of the
 * three day names the festival has ever had.
 */
private val RAIL_WIDTH = 84.dp
