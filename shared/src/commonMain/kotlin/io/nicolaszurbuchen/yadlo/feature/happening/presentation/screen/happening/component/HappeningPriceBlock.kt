package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.HappeningPriceUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * What it costs. One row per tier, the label on the left and the amount on the right.
 *
 * **The deposit is never added to a tier.** The Silent Party is CHF 25 with a CHF 50 headset deposit
 * — a single CHF 75 would be wrong in the direction that stops someone coming. It sits on its own
 * line, under the tiers, with the note that says when it is taken.
 */
@Composable
fun HappeningPriceBlock(
    price: HappeningPriceUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
        modifier = modifier.fillMaxWidth(),
    ) {
        price.tiers.forEach { tier ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = tier.label.orEmpty(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.appColors.textSecondary,
                )

                Text(
                    text = tier.amount.asString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.appColors.textPrimary,
                )
            }
        }

        price.deposit?.let { deposit ->
            Text(
                text = deposit.asString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.appColors.textSecondary,
            )
        }

        price.depositNote?.let { note ->
            Text(
                text = note,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.appColors.textTertiary,
            )
        }
    }
}
