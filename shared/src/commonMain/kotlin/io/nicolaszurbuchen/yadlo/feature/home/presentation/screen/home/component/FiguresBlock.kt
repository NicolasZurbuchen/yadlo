package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.component

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.HomeBlockUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/** Two per row because the labels are phrases — "heures de DJ sets et concerts" — not words. */
private const val FIGURES_PER_ROW = 2

/** The closing figures of the edition just finished, the ENDED half of story 62. */
@Composable
fun FiguresBlock(
    block: HomeBlockUiModel.Figures,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = block.title.asString(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.appColors.textPrimary,
        )

        block.items.chunked(FIGURES_PER_ROW).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
                row.forEach { figure ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier =
                            Modifier
                                .weight(1f)
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.appColors.surfaceRaised)
                                .padding(MaterialTheme.spacing.md),
                    ) {
                        Text(
                            text = figure.value,
                            style = MaterialTheme.typography.headlineSmall,
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

                // Keeps a lone figure on the last row the same width as the ones above it rather
                // than letting it stretch across the screen.
                repeat(FIGURES_PER_ROW - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
