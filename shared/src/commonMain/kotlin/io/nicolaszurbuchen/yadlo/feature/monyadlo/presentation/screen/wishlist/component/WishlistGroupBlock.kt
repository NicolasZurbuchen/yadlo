package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist.WishlistGroupUiModel

/**
 * One Category of the Wishlist, with its saved Stands under it.
 *
 * Category is the app's only grouping axis, so *restauration* and *créateurs* here are the same
 * groups the Programme filters by rather than a second taxonomy grown for one screen.
 */
@Composable
fun WishlistGroupBlock(
    group: WishlistGroupUiModel,
    onStandClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = group.name.uppercase(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.appColors.textTertiary,
            modifier =
                Modifier.padding(
                    horizontal = MaterialTheme.spacing.md,
                    vertical = MaterialTheme.spacing.sm,
                ),
        )

        group.stands.forEachIndexed { index, stand ->
            if (index > 0) {
                HorizontalDivider(color = MaterialTheme.appColors.borderSubtle)
            }

            WishlistStandRow(stand = stand, onClick = onStandClick)
        }
    }
}
