package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.component

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.AnnouncementUiModel
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.HomeBlockUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * The only block that appears in all five phases, and the reason to open the app the rest of the
 * year (story 57).
 *
 * A card with no URL is not clickable and carries no affordance, which is story 85 read literally:
 * plainly untappable beats a tap that does nothing.
 */
@Composable
fun AnnouncementsBlock(
    block: HomeBlockUiModel.Announcements,
    onAnnouncementClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = block.title.asString(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.appColors.textPrimary,
        )

        block.items.forEach { item ->
            AnnouncementCard(item = item, onClick = onAnnouncementClick)
        }
    }
}

@Composable
private fun AnnouncementCard(
    item: AnnouncementUiModel,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.appColors.surfaceRaised)
                .then(
                    if (item.url != null) {
                        Modifier.clickable { onClick(item.url) }
                    } else {
                        Modifier
                    },
                )
                .padding(MaterialTheme.spacing.md),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.dateText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.appColors.textTertiary,
            )

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.appColors.textPrimary,
                modifier = Modifier.padding(top = MaterialTheme.spacing.xs),
            )

            item.body?.let { body ->
                Text(
                    text = body,
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
