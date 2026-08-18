package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.component.YadloDietaryTags
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.HappeningMenuGroupUiModel

/**
 * One group of a stand's menu — *Plats*, *Boissons*, *Desserts*.
 *
 * Each item is up to three independent rows: the name with its price, the description, the marks.
 * Nothing shares a line with the name, so nothing can overflow into the price — an item with only a
 * name and a price is a complete item, which is the data most trucks actually give.
 *
 * The source line is not decoration. No menu here is confirmed by the festival: one is a vendor's
 * carte for another location, one was read off a photographed chalkboard. A price presented as fact
 * when it came off a blackboard is the kind of wrong that costs someone at the counter.
 */
@Composable
fun HappeningMenuGroupBlock(
    group: HappeningMenuGroupUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = group.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.appColors.textPrimary,
        )

        group.description?.let { description ->
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.appColors.textSecondary,
            )
        }

        group.items.forEach { item ->
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
                modifier = Modifier.fillMaxWidth().padding(top = MaterialTheme.spacing.xs),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.appColors.textPrimary,
                        modifier = Modifier.weight(1f, fill = false),
                    )

                    item.priceText?.let { priceText ->
                        Text(
                            text = priceText,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.appColors.textSecondary,
                        )
                    }
                }

                item.description?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                }

                // A glyph and the word beside it, never the glyph alone: the picture is what is
                // found while scanning a carte of fourteen dishes, and the word is what makes it
                // safe to act on — no legend to learn, and no symbol that means "contains" in one
                // country and "free from" in another.
                if (item.dietary.isNotEmpty()) {
                    YadloDietaryTags(tags = item.dietary)
                }
            }
        }

        group.source?.let { source ->
            Text(
                text = source,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.appColors.textTertiary,
                modifier = Modifier.padding(top = MaterialTheme.spacing.xs),
            )
        }
    }
}
