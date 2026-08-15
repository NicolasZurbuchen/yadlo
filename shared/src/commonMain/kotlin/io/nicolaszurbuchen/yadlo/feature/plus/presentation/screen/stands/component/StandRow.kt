package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.StandUiModel

/**
 * One Stand on the browse list. Opens the fiche, which holds the menu and the single heart that
 * puts it on the Wishlist.
 *
 * **A card, and no Category colour on it.** Cards because *lists compare, cards separate* and these
 * are separate places rather than a timeline to read across — the same rule that gives the
 * Programme rows and this screen tiles. No colour because the five measured hues belong to what
 * kind of thing a Happening is, and a second colour system on the same screen makes both of them
 * mean less.
 */
@Composable
fun StandRow(
    stand: StandUiModel,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.appColors.surface)
                .clickable { onClick(stand.id) }
                .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = stand.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.appColors.textPrimary,
            )

            stand.offering?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.appColors.textSecondary,
                )
            }

            stand.marks?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.appColors.textTertiary,
                )
            }
        }

        Text(
            text = DISCLOSURE,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.appColors.textTertiary,
        )
    }
}

private const val DISCLOSURE = "›"
