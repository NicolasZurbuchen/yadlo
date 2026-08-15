package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story.StoryFigureUiModel

/**
 * *Yadlo en chiffres*, side by side.
 *
 * Three tiles across rather than three rows down, because the numbers are read against each other
 * — six thousand visitors, a hundred and sixty volunteers — and stacked they stop being a
 * comparison and start being a list.
 */
@Composable
fun FigureRow(
    figures: List<StoryFigureUiModel>,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier = modifier.fillMaxWidth(),
    ) {
        figures.forEach { figure ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = figure.value,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.appColors.textPrimary,
                )

                Text(
                    text = figure.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.appColors.textSecondary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
