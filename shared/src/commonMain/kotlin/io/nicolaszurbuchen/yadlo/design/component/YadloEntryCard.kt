package io.nicolaszurbuchen.yadlo.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.spacing
import io.nicolaszurbuchen.yadlo.design.uimodel.YadloEntryUiModel
import io.nicolaszurbuchen.yadlo.infra.text.asString
import org.jetbrains.compose.resources.stringResource

/**
 * A titled group of rows on one raised card — the shape of the Plus tab, and now of the block on
 * Accueil that promotes a few of its screens by Phase.
 *
 * **Cards here, rows on the Programme** — the rule is that lists compare and cards separate. The
 * groups of Plus are not alternatives to weigh against one another; they are separate drawers, and
 * measured against the Programme the card style costs 32% more vertical space, which is the price
 * worth paying on a list of twenty and not on a list of fifty.
 *
 * The header is inset from the card rather than flush with it, which is what makes the card read as
 * a block the header labels instead of a box the header is inside.
 *
 * Dividers are drawn *between* rows rather than under them, because which row is last depends on
 * what the content published — a trailing hairline would appear and vanish with a section nobody
 * edited.
 *
 * **Generic over [T] rather than taking a list of [YadloEntryUiModel] with ids on it.** Each caller
 * keeps its own row type — a Plus row, a promoted tile — and hands over only how to *draw* one
 * through [entryOf]; [onEntryClick] gives that same value back untouched. Nothing has to be matched
 * up by a string afterwards, and no callback rides on a UI model.
 */
@Composable
fun <T> YadloEntryCard(
    title: String,
    items: List<T>,
    entryOf: (T) -> YadloEntryUiModel,
    onEntryClick: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier = modifier.fillMaxWidth(),
    ) {
        YadloSectionHeader(
            title = title,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.sm),
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.appColors.surface),
        ) {
            items.forEachIndexed { index, item ->
                if (index > 0) {
                    HorizontalDivider(color = MaterialTheme.appColors.borderSubtle)
                }

                YadloEntryRow(entry = entryOf(item), onClick = { onEntryClick(item) })
            }
        }
    }
}

/**
 * The leading icon is decorative and carries no `contentDescription`: the label beside it says the
 * same thing in words, and a screen reader announcing "restaurant, Nourriture & boissons" reads the
 * row twice. The trailing mark is the opposite case for the rows that leave the app — see
 * [io.nicolaszurbuchen.yadlo.design.uimodel.YadloLinkMarkUiModel].
 */
@Composable
private fun YadloEntryRow(
    entry: YadloEntryUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = ROW_MIN_HEIGHT)
                .clickable { onClick() }
                .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
    ) {
        Icon(
            imageVector = entry.icon,
            contentDescription = null,
            tint = MaterialTheme.appColors.textSecondary,
            modifier = Modifier.size(ICON_SIZE),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = entry.title.asString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.appColors.textPrimary,
            )

            entry.subtitle?.let {
                Text(
                    text = it.asString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.appColors.textSecondary,
                )
            }
        }

        Icon(
            imageVector = entry.mark.icon,
            contentDescription = entry.mark.contentDescription?.let { stringResource(it) },
            tint = MaterialTheme.appColors.textTertiary,
            modifier = Modifier.size(ICON_SIZE),
        )
    }
}

// Matched to the label's line height rather than to Material's 24dp default: at 24 the icon
// outweighs the word next to it, and these lists are read by their words.
private val ICON_SIZE = 20.dp

// The natural height of a row that has a subtitle, applied to every row so a card of sixteen does
// not comb up and down as the content publishes one line here and two there. A minimum rather than
// a fixed height: at the largest accessibility text sizes a two-line row has to be allowed to grow,
// and clipping the subtitle to keep the rhythm would be the wrong trade.
private val ROW_MIN_HEIGHT = 64.dp
