package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.spacing
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
 *
 * The rows run to the block's own edges rather than inside its padding, so a tap ripples across the
 * whole width and the rule between two annonces is a rule between two annonces rather than a line
 * floating in the middle of one. Each row pads itself instead — see [AnnouncementCard].
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
        contentPadding = PaddingValues(0.dp),
        modifier = modifier,
    ) {
        block.items.forEachIndexed { index, item ->
            if (index > 0) {
                // Inset, where the ripple is not: the rule is a mark between two rows and reads as
                // one when it stops short of the border, while a tap that lights up anything less
                // than the full row reads as a misfire.
                HorizontalDivider(
                    color = MaterialTheme.appColors.borderSubtle,
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                )
            }

            AnnouncementCard(item = item, onClick = onAnnouncementClick)
        }
    }
}
