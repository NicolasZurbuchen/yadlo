package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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

/**
 * The reason to keep the app installed on the 361 days when nothing is happening (story 56).
 *
 * The cells are weighted equally rather than sized to their contents, so the numbers do not shuffle
 * sideways every second as the digits change width.
 */
@Composable
fun CountdownBlock(
    block: HomeBlockUiModel.Countdown,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.appColors.primarySubtle)
                .padding(vertical = MaterialTheme.spacing.lg, horizontal = MaterialTheme.spacing.md),
    ) {
        Text(
            text = block.title.asString(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.appColors.onPrimarySubtle,
        )

        Text(
            text = block.editionName,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.appColors.onPrimarySubtle,
            modifier = Modifier.padding(top = MaterialTheme.spacing.xs),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            modifier = Modifier.fillMaxWidth().padding(top = MaterialTheme.spacing.md),
        ) {
            block.cells.forEach { cell ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = cell.value,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.appColors.onPrimarySubtle,
                    )
                    Text(
                        text = cell.label.asString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.appColors.onPrimarySubtle,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
