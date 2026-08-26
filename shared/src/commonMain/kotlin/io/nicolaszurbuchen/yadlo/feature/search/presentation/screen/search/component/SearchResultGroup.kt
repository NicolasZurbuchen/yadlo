package io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.component.YadloSectionHeader
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.categoryColors
import io.nicolaszurbuchen.yadlo.app.design.theme.sizing
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloLinkMarkUiModel
import io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.SearchGroupUiModel
import io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.SearchRowUiModel
import io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.uimodel.SearchTopicUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.asString
import org.jetbrains.compose.resources.stringResource

/**
 * One heading and the rows under it, on the card the Plus tab and Accueil already use.
 *
 * **Both kinds of result are drawn by the same row**, because a list where a stand and a payment
 * page had different shapes would be asking the reader to learn two things at the moment they are
 * scanning for one. What differs is only the leading mark, which is the honest difference: a
 * Happening is content and carries its Category's colour, a practical answer is a screen and carries
 * the icon that screen wears on the Plus tab.
 *
 * The Category is written out beside the square rather than left to it — the same rule the stand
 * cards follow, and for the same reason: in July sun, on a phone, the word is what survives.
 */
@Composable
fun SearchResultGroup(
    group: SearchGroupUiModel,
    onHappeningClick: (String) -> Unit,
    onTopicClick: (SearchTopicUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier = modifier.fillMaxWidth(),
    ) {
        YadloSectionHeader(
            title = group.title.asString(),
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.sm),
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.appColors.surface),
        ) {
            group.rows.forEachIndexed { index, row ->
                if (index > 0) {
                    HorizontalDivider(color = MaterialTheme.appColors.borderSubtle)
                }

                SearchResultRow(
                    row = row,
                    onClick = {
                        when (row) {
                            is SearchRowUiModel.Happening -> onHappeningClick(row.happeningId)
                            is SearchRowUiModel.Practical -> onTopicClick(row.topic)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    row: SearchRowUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = MaterialTheme.sizing.rowMinHeight)
                .clickable { onClick() }
                .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
    ) {
        // One box either way, so the titles line up down the list whichever kind of row they are on.
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(MaterialTheme.sizing.iconMd)) {
            when (row) {
                is SearchRowUiModel.Happening -> {
                    Box(
                        modifier =
                            Modifier
                                .size(MaterialTheme.sizing.categoryMark)
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(MaterialTheme.categoryColors.forId(row.categoryId).fill),
                    )
                }

                is SearchRowUiModel.Practical -> {
                    Icon(
                        imageVector = row.topic.icon,
                        // Decorative: the title beside it names the screen the icon stands for.
                        contentDescription = null,
                        tint = MaterialTheme.appColors.textSecondary,
                        modifier = Modifier.size(MaterialTheme.sizing.iconMd),
                    )
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text =
                    when (row) {
                        is SearchRowUiModel.Happening -> row.name
                        is SearchRowUiModel.Practical -> row.title.asString()
                    },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.appColors.textPrimary,
            )

            // The Category, and what matched when it was not the name — "Restauration · Ragoût de
            // tofu". One line rather than two, because a result is scanned rather than read, and
            // ellipsised rather than wrapped for the same reason.
            if (row is SearchRowUiModel.Happening) {
                Text(
                    text = row.reason?.let { "${row.categoryName} · $it" } ?: row.categoryName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.appColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Icon(
            imageVector = YadloLinkMarkUiModel.DISCLOSURE.icon,
            contentDescription = YadloLinkMarkUiModel.DISCLOSURE.contentDescription?.let { stringResource(it) },
            tint = MaterialTheme.appColors.textTertiary,
            modifier = Modifier.size(MaterialTheme.sizing.iconMd),
        )
    }
}
