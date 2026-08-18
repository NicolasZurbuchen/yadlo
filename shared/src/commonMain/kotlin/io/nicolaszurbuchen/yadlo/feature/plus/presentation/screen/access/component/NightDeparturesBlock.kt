package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access.AccessNightUiModel

/**
 * One night of the *bus pyjama*, written as a night rather than as a list of buses.
 *
 * Seven departures fit in two blocks instead of seven rows, which is the difference between a
 * screen you can read at 02:00 and one you have to scroll.
 *
 * **The times are the largest thing on the screen**, set at the same size as a hero's own headline.
 * They were at label size, which is right for a Slot on a Programme row and wrong here: this is the
 * one line on *Accès* somebody reads standing on a dark road, and everything else on the page —
 * which lines stop where, how long the walk is — was already known before leaving the house. The
 * display face and its tabular figures keep the two nights lining up under each other.
 *
 * A rule closes each night, including the last, because what follows the last one is the note that
 * qualifies a departure inside it. A footnote sharing a block with the times it warns about reads
 * as another departure.
 */
@Composable
fun NightDeparturesBlock(
    night: AccessNightUiModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            modifier = Modifier.fillMaxWidth().padding(bottom = MaterialTheme.spacing.sm),
        ) {
            Text(
                text = night.night.uppercase(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.appColors.textTertiary,
            )

            Text(
                text = night.times,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.appColors.textPrimary,
            )
        }

        HorizontalDivider(color = MaterialTheme.appColors.borderSubtle)

        night.notes.forEach { note ->
            Text(
                text = note,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.appColors.textSecondary,
                modifier = Modifier.padding(top = MaterialTheme.spacing.sm),
            )
        }
    }
}
