package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component

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
import io.nicolaszurbuchen.yadlo.app.design.component.YadloDietaryMarks
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.HappeningMenuGroupUiModel

/**
 * The dishes in one group of a stand's menu — *Plats*, *Boissons*, *Desserts*.
 *
 * **The group's name is not drawn here.** It is the section header the screen wraps this in, at the
 * same level as *Quand* and *Tarifs*, because that is what a group is: a section of the fiche.
 * There is no *Au menu* above them any more — two levels of heading over fourteen dishes was one
 * more than the carte needs, and the outer one only said what the dishes underneath already do.
 *
 * Each dish is up to three things: the name with its marks and its price, then the ingredients.
 * Nothing shares a line with the price, so nothing can overflow into it — an item with only a name
 * and a price is a complete item, which is the data most trucks actually give.
 *
 * **The marks are glyphs, and the words for them are at the top of the fiche.** See
 * [YadloDietaryMarks] for what that trades. The short version: a carte of fourteen dishes cannot
 * spell out the whole vocabulary fourteen times without the marks outweighing the food.
 *
 * **No source line.** *Reversed: every group carried one.* It said the carte was reconstructed from
 * the vendor's own and not confirmed by the festival — true, and on the screen it was doing the
 * opposite of its job: five groups of a menu ended in five copies of the same forty-word
 * disclaimer, which is how a reader learns to skip small grey text rather than how they learn a
 * price might be wrong. Provenance is still on every group and every item in the content, which is
 * where the record belongs.
 */
@Composable
fun HappeningMenuGroupBlock(
    group: HappeningMenuGroupUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
        modifier = modifier.fillMaxWidth(),
    ) {
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
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f),
                    ) {
                        // A title, not body text. At `bodyLarge` a dish name was 15sp Normal over a
                        // 13sp Normal ingredient line — two points and a shade of grey apart, in
                        // the same weight, which on a carte of fourteen dishes gave no edge to
                        // scan down. `titleMedium` puts a step of weight between them as well.
                        //
                        // Shrinks to what is left rather than filling it, so the glyphs sit against
                        // the end of the name instead of against the price.
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.appColors.textPrimary,
                            modifier = Modifier.weight(1f, fill = false),
                        )

                        if (item.dietary.isNotEmpty()) {
                            YadloDietaryMarks(tags = item.dietary)
                        }
                    }

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
                        // Under the name and clear of the price column, so a two-line ingredient
                        // list does not read as a caption on the number.
                        modifier = Modifier.padding(end = MaterialTheme.spacing.xl),
                    )
                }
            }
        }
    }
}
