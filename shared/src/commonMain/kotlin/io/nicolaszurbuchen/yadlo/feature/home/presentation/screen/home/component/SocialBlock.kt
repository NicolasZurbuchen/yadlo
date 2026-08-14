package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.HomeBlockUiModel

/**
 * The four networks, quietly, at the foot of the screen.
 *
 * Deliberately not a feed. A real Instagram feed needs a Business account and a Meta app authorised
 * by the association, which is out of reach while the app is unofficial — so the networks are links
 * out, and the names are the ones the content gives rather than icons the app would have to ship
 * for a platform nobody has heard of yet.
 *
 * A [FlowRow] rather than a Row because four names at the largest accessibility text size do not
 * fit on one line, and this is the last thing on the screen — it may wrap, it may not truncate.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SocialBlock(
    block: HomeBlockUiModel.Social,
    onSocialClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm, Alignment.CenterHorizontally),
        modifier = modifier.fillMaxWidth(),
    ) {
        block.items.forEach { item ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .clip(MaterialTheme.shapes.extraSmall)
                        .clickable { onSocialClick(item.url) }
                        .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs),
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = MaterialTheme.appColors.textSecondary,
                    modifier = Modifier.size(SOCIAL_ICON_SIZE),
                )
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.appColors.textSecondary,
                )
            }
        }
    }
}

private val SOCIAL_ICON_SIZE = 18.dp
