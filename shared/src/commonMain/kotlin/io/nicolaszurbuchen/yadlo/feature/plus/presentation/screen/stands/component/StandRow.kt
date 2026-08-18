package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.component.YadloDietaryTags
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloLinkMarkUiModel
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
 *
 * **Built to [YadloLinkTile]'s geometry**, down to the chevron. It was a third shorter for no
 * reason other than being written earlier, which left a column of stands looking cramped next to
 * every other tappable card in the app — and this is the one the visitor scrolls forty of.
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
                .heightIn(min = ROW_MIN_HEIGHT)
                .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.md),
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

            if (stand.dietary.isNotEmpty()) {
                YadloDietaryTags(tags = stand.dietary)
            }
        }

        Icon(
            imageVector = YadloLinkMarkUiModel.DISCLOSURE.icon,
            contentDescription = null,
            tint = MaterialTheme.appColors.textTertiary,
            modifier = Modifier.size(MARK_SIZE),
        )
    }
}

// Both taken from YadloLinkTile rather than re-derived: the mark reads as punctuation on the label
// at this size, and the minimum is what a Plus row takes. A stand with no offering line and one
// with two should not be visibly different objects.
private val MARK_SIZE = 20.dp
private val ROW_MIN_HEIGHT = 64.dp
