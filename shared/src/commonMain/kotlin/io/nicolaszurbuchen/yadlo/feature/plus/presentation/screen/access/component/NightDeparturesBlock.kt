package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
 * screen you can read at 02:00 and one you have to scroll. The times take the display face and its
 * tabular figures, so the two nights line up under each other.
 */
@Composable
fun NightDeparturesBlock(
    night: AccessNightUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = night.night.uppercase(),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.appColors.textTertiary,
        )

        Text(
            text = night.times,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.appColors.textPrimary,
        )

        night.notes.forEach { note ->
            Text(
                text = note,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.appColors.textSecondary,
            )
        }
    }
}
