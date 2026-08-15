package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.component

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.feature.home.presentation.component.AnnouncementCard
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.HomeBlockUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.home_announcements_all

/**
 * The only block that appears in all five phases, and the reason to open the app the rest of the
 * year (story 57).
 *
 * A summary, not the feed: the two most recent, with the way to the rest in the header. The action
 * is absent when there is no rest, because a button opening what you are already reading is the
 * same problem as one that does nothing.
 */
@Composable
fun AnnouncementsBlock(
    block: HomeBlockUiModel.Announcements,
    onAnnouncementClick: (String) -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionBlock(
        title = block.title,
        actionLabel = if (block.hasMore) UiText.Resource(Res.string.home_announcements_all) else null,
        onActionClick = if (block.hasMore) onSeeAllClick else null,
        modifier = modifier,
    ) {
        block.items.forEachIndexed { index, item ->
            if (index > 0) {
                HorizontalDivider(color = MaterialTheme.appColors.borderSubtle)
            }

            AnnouncementCard(item = item, onClick = onAnnouncementClick)
        }
    }
}
