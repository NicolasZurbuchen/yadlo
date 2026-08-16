package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
 * *Yadlo en chiffres*, two to a row.
 *
 * Read against each other rather than down a list — six thousand visitors, a hundred and sixty
 * volunteers — so they are laid side by side. **Two columns rather than as many as there are
 * figures**: the numbers are set in the display face and three across a phone leaves each of them a
 * third of the width, which is where "3200 litres de bière" wraps its label onto three lines and the
 * comparison the block exists for stops being legible. Two is the widest a figure can be given and
 * still be a figure.
 *
 * Laid out by hand rather than with a lazy grid, because this sits inside the page's own vertical
 * scroll and a nested lazy container in an infinite-height parent is a crash rather than a layout.
 * The count is three, so the cost of not being lazy is nothing.
 */
@Composable
fun FigureGrid(
    figures: List<StoryFigureUiModel>,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
        modifier = modifier.fillMaxWidth(),
    ) {
        figures.chunked(COLUMNS).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                modifier = Modifier.fillMaxWidth(),
            ) {
                row.forEach { figure ->
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

                // An odd last figure keeps its own column rather than spreading across the row. It
                // is the same size as the two above it, which is what makes the three read as one
                // block instead of as a pair and an afterthought.
                repeat(COLUMNS - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

private const val COLUMNS = 2
