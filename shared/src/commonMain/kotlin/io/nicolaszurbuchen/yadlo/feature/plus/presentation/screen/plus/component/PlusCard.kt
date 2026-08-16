package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.PlusEntry
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.PlusGroupUiModel
import org.jetbrains.compose.resources.stringResource

/**
 * One group of the root list: a header on the page ground, and its rows on a raised card.
 *
 * **Cards here, rows on the Programme** — the rule is that lists compare and cards separate. These
 * four groups are not alternatives to weigh against one another; they are separate drawers, and
 * measured against the Programme the card style costs 32% more vertical space, which is exactly the
 * price worth paying on a list of twenty and not on a list of fifty.
 *
 * Dividers are drawn between rows rather than under them, because which row is last depends on what
 * the content published — a trailing hairline would appear and vanish with a section nobody edited.
 */
@Composable
fun PlusCard(
    group: PlusGroupUiModel,
    onEntryClick: (PlusEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier = modifier.fillMaxWidth(),
    ) {
        // The header is inset from the card rather than flush with it, which is what makes the card
        // read as a block the header labels instead of a box the header is inside.
        Text(
            text = stringResource(group.id.title).uppercase(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.appColors.textTertiary,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.sm),
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.appColors.surface),
        ) {
            group.rows.forEachIndexed { index, row ->
                if (index > 0) {
                    HorizontalDivider(color = MaterialTheme.appColors.borderSubtle)
                }

                PlusRow(row = row, onClick = { onEntryClick(row.entry) })
            }
        }
    }
}
