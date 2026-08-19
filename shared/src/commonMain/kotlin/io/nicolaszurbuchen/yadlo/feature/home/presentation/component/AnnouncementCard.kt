package io.nicolaszurbuchen.yadlo.feature.home.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.home.presentation.uimodel.AnnouncementUiModel

/**
 * One annonce, on Accueil and on the full list alike.
 *
 * No card of its own: it sits inside a block that is already a surface, and a card within a card is
 * two borders saying one thing. A card with no URL is not clickable and carries no affordance,
 * which is story 85 read literally — plainly untappable beats a tap that does nothing.
 *
 * **It pads itself, and its container does not.** The ripple and the rules between annonces have to
 * reach the edge of whatever holds the row — a tap that lights up a strip 16dp short of the border
 * looks like a misfire. So the inset lives here, where it is inside the touch target rather than
 * around it.
 */
@Composable
fun AnnouncementCard(
    item: AnnouncementUiModel,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        // The icon sat against the last word of the body. It qualifies the whole card rather than
        // that word, and at nothing they read as one line.
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
        modifier =
            modifier
                .fillMaxWidth()
                .then(
                    if (item.url != null) {
                        Modifier.clickable { onClick(item.url) }
                    } else {
                        Modifier
                    },
                )
                .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.md),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.dateText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.appColors.textTertiary,
            )

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.appColors.textPrimary,
                modifier = Modifier.padding(top = MaterialTheme.spacing.xs),
            )

            if (item.body.isNotBlank()) {
                Text(
                    text = item.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.appColors.textSecondary,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.xs),
                )
            }
        }

        if (item.url != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                contentDescription = null,
                tint = MaterialTheme.appColors.textTertiary,
                modifier = Modifier.align(Alignment.CenterVertically).size(LINK_ICON_SIZE),
            )
        }
    }
}

private val LINK_ICON_SIZE = 20.dp
