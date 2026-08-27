package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.HappeningPriceUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * What it costs. The amount first, whatever it applies to underneath it.
 *
 * **Left-aligned, under the section header, rather than pinned to the right edge.** The tiers used
 * to be a label on the left and an amount on the right, which is how a receipt is laid out — and a
 * receipt is a list of things you have already decided to buy. Here the number *is* the answer, and
 * most Happenings publish exactly one of them with no label at all, so pinning it right left the
 * single fact the section exists to state sitting alone against the far margin with nothing to
 * align with.
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
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier = modifier.fillMaxWidth(),
    ) {
        price.tiers.forEach { tier ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = tier.amount.asString(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.appColors.textPrimary,
                )

                // Only when the content says who the tier is for. Most publish one price for
                // everyone, and an invented "plein tarif" under it would be a claim, not a label.
                tier.label?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                }
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
