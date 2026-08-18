package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.component.YadloDietaryTags
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist.WishlistStandUiModel

/**
 * One saved Stand. The name, what it sells, and how it sells it.
 *
 * Rows rather than cards, for the reason the Programme uses them: this is a list you compare across
 * — which of the four you kept you actually want to walk to — and cards separate exactly that.
 *
 * The row opens the Stand's fiche, which holds the menu and the one heart that can remove it again.
 */
@Composable
fun WishlistStandRow(
    stand: WishlistStandUiModel,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick(stand.id) }
                .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
    ) {
        Text(
            text = stand.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.appColors.textPrimary,
        )

        stand.offering?.let { offering ->
            Text(
                text = offering,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.appColors.textSecondary,
            )
        }

        if (stand.dietary.isNotEmpty()) {
            YadloDietaryTags(tags = stand.dietary)
        }
    }
}
