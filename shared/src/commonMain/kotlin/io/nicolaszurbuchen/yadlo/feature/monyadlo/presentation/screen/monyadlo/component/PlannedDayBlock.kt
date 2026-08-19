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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo.MonYadloDayUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * One day of the Plan: the date on a rail down the left, its Slots scrolling past to the right.
 *
 * The rail is what the layout was chosen for — DECISIONS.md § Mon Yadlo layout measured rows with
 * sticky headers at 829px, this at 882px and cards at 964px. The 6% extra scrolling buys a date that
 * is visible the whole time you are reading the day it belongs to, which a header loses the moment
 * it scrolls off.
 *
 * **The date is stacked and centred, with the day itself the largest thing on the rail.** It was a
 * weekday over `11.07`, both small, which made the rail a label rather than a landmark — and the
 * rail's whole reason for existing is to be findable while you are reading the day it belongs to.
 * The weekday stays on top because it is what someone thinks in ("le samedi"); the month goes
 * underneath in words, which is what stops a bare 11 from being ambiguous the year the festival
 * moves.
 *
 * Centred rather than flush left, because the three lines are three different widths of the same
 * thing: against a left edge they read as a ragged list, and around a middle they read as a date.
 *
 * The weekday is cut to three letters, which is what lets the rail be narrow — a rail sized for
 * `VENDREDI` was 84dp of mostly nothing on the two days whose names are shorter.
 */
@Composable
fun PlannedDayBlock(
    day: MonYadloDayUiModel,
    onRowClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth().padding(horizontal = MaterialTheme.spacing.md)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            // No arrangement of its own: the three lines are one date, and a gap between them makes
            // them read as three separate facts about the same day.
            modifier = Modifier.width(RAIL_WIDTH).padding(top = MaterialTheme.spacing.sm),
        ) {
            Text(
                // Three letters, from whatever the content calls the day. Cut rather than mapped,
                // so a day the association names something else still shortens to something.
                text = day.name.take(WEEKDAY_LETTERS).uppercase(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.appColors.textSecondary,
                textAlign = TextAlign.Center,
            )

            Text(
                text = day.dayNumber,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.appColors.textPrimary,
                textAlign = TextAlign.Center,
            )

            Text(
                text = day.monthName.asString(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.appColors.textTertiary,
                textAlign = TextAlign.Center,
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
 * The month is the widest line now that the weekday is three letters, and `juillet` at 14sp fits
 * this with room. A month the festival has never run in — `septembre` is the longest French one —
 * wraps rather than clips, which is the right way for a constant like this to be wrong.
 */
private val RAIL_WIDTH = 64.dp

/** Enough to tell `VEN` from `SAM` from `DIM`, which is all three of them. */
private const val WEEKDAY_LETTERS = 3
