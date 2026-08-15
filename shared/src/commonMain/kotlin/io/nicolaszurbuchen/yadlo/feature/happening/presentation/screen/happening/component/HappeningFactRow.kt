package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * One line of *Bon à savoir* — material provided, supervision, who it suits.
 *
 * **Facts must not look tappable.** A leading `✓` on the page background, never the card-with-
 * chevron style, which is reserved for things that navigate. The moment a fact looks like a row you
 * can open, every fact on the screen becomes one the reader has to test.
 */
@Composable
fun HappeningFactRow(
    fact: UiText,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = FACT_MARK,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.appColors.textTertiary,
        )

        Text(
            text = fact.asString(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.appColors.textSecondary,
        )
    }
}

private const val FACT_MARK = "✓"
