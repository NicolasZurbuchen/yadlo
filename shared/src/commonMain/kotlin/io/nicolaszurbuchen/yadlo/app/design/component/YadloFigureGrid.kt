package io.nicolaszurbuchen.yadlo.app.design.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.app.design.uimodel.FigureUiModel

/**
 * *Yadlo en chiffres*, two to a row, with the caveat the numbers owe the reader underneath.
 *
 * **One component rather than a grid on Accueil and another in *L'histoire*.** Both screens print
 * the same three numbers out of the same edition, and being written months apart had already put
 * the figures in two different colours — the kind of drift nobody files a bug about and everybody
 * sees.
 *
 * Read against each other rather than down a list, so they are laid side by side. **Two columns
 * rather than as many as there are figures**: the numbers are set in the display face and three
 * across a phone leaves each of them a third of the width, which is where "3200 litres de bière"
 * wraps its label onto three lines and the comparison the block exists for stops being legible.
 *
 * Laid out by hand rather than with a lazy grid, because both call sites sit inside a page's own
 * vertical scroll and a nested lazy container in an infinite-height parent is a crash rather than a
 * layout. The count is three, so the cost of not being lazy is nothing.
 *
 * [caveat] is shown rather than hidden. The association has published closing figures exactly once,
 * so a block that waited for confirmed ones would be empty for most of its life — naming where these
 * came from is what makes showing them honest. It is set apart from the last row of labels, because
 * it qualifies all of the figures and a caption tight under one column reads as belonging to that
 * column alone.
 */
@Composable
fun YadloFigureGrid(
    figures: List<FigureUiModel>,
    modifier: Modifier = Modifier,
    caveat: String? = null,
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
                            color = MaterialTheme.appColors.primary,
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

        caveat?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.appColors.textTertiary,
                modifier = Modifier.fillMaxWidth().padding(top = MaterialTheme.spacing.sm),
            )
        }
    }
}

private const val COLUMNS = 2
