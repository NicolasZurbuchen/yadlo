package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.mon_yadlo_wishlist_empty
import yadlo.shared.generated.resources.mon_yadlo_wishlist_title

/**
 * The way to the other half of Mon Yadlo — one full-width tile, never a second tab or a segmented
 * control (DECISIONS.md § Two verbs: Plan and Wishlist).
 *
 * It sits **above** the timeline. *À essayer* is a checklist consulted while standing on the site,
 * and by the Sunday the timeline above it would be three days of finished rows to scroll past.
 *
 * The count is a bare numeral rather than "3 stands", which keeps it out of the plural agreement
 * that a two-language build would otherwise have to carry for one label. Zero says so in words
 * instead, because a lone `0` is the one number that reads as a fault.
 */
@Composable
fun WishlistTile(
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.appColors.surface)
                .clickable { onClick() }
                .padding(MaterialTheme.spacing.md),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = stringResource(Res.string.mon_yadlo_wishlist_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.appColors.textPrimary,
            )

            if (count == 0) {
                Text(
                    text = stringResource(Res.string.mon_yadlo_wishlist_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.appColors.textTertiary,
                )
            }
        }

        if (count > 0) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.appColors.primary,
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.appColors.textTertiary,
        )
    }
}
