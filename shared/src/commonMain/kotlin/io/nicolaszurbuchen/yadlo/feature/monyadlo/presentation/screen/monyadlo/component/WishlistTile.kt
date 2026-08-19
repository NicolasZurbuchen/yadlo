package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.mon_yadlo_wishlist_empty
import yadlo.shared.generated.resources.mon_yadlo_wishlist_subtitle_one
import yadlo.shared.generated.resources.mon_yadlo_wishlist_subtitle_other
import yadlo.shared.generated.resources.mon_yadlo_wishlist_title

/**
 * The way to the other half of Mon Yadlo — one full-width tile, never a second tab or a segmented
 * control (DECISIONS.md § Two verbs: Plan and Wishlist).
 *
 * It sits **above** the timeline. *À essayer* is a checklist consulted while standing on the site,
 * and by the Sunday the timeline above it would be three days of finished rows to scroll past.
 *
 * **A Plus row, not a statistic.** The count was set as a bare numeral at 36sp on the right, which
 * is the size the app uses for "3200 litres de bière" — and three saved stands is not a figure about
 * the festival, it is a note about what you are going to eat. It is a sentence in the subtitle now,
 * under the same leading icon *Nourriture & boissons* wears in Plus, so the two rows that lead to
 * the same subject look like they do.
 *
 * The plural is two strings rather than one with a `(s)`: French puts 1 in the singular, and the
 * zero case never reaches them — it says so in words instead, because a lone `0` is the one number
 * that reads as a fault.
 */
@Composable
fun WishlistTile(
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.appColors.surface)
                .clickable { onClick() }
                .padding(MaterialTheme.spacing.md),
    ) {
        // Decorative: the label beside it says the same thing, and the same icon on the Plus row
        // this leads back to carries no description either.
        Icon(
            imageVector = Icons.Outlined.Restaurant,
            contentDescription = null,
            tint = MaterialTheme.appColors.textSecondary,
            modifier = Modifier.size(ICON_SIZE),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = stringResource(Res.string.mon_yadlo_wishlist_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.appColors.textPrimary,
            )

            Text(
                text =
                    when (count) {
                        0 -> stringResource(Res.string.mon_yadlo_wishlist_empty)
                        1 -> stringResource(Res.string.mon_yadlo_wishlist_subtitle_one, count)
                        else -> stringResource(Res.string.mon_yadlo_wishlist_subtitle_other, count)
                    },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.appColors.textSecondary,
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.appColors.textTertiary,
            modifier = Modifier.size(ICON_SIZE),
        )
    }
}

// Matched to the label's line height rather than Material's 24dp default, the same as every icon on
// a Plus row: at 24 the icon outweighs the word beside it.
private val ICON_SIZE = 20.dp
