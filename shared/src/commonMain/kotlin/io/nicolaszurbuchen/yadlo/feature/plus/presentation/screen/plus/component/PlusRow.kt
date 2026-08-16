package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.component

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
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.PlusRowUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.asString
import org.jetbrains.compose.resources.stringResource

/**
 * One entry of the root list. Icon, name, sometimes a line of what is behind it, and a chevron.
 *
 * The icon is decorative and carries no `contentDescription`: the label beside it says the same
 * thing in words, and a screen reader announcing "restaurant, Nourriture & boissons" reads the row
 * twice.
 */
@Composable
fun PlusRow(
    row: PlusRowUiModel,
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
            imageVector = row.entry.icon,
            contentDescription = null,
            tint = MaterialTheme.appColors.textSecondary,
            modifier = Modifier.size(ICON_SIZE),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = stringResource(row.entry.title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.appColors.textPrimary,
            )

            row.subtitle?.let {
                Text(
                    text = it.asString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.appColors.textSecondary,
                )
            }
        }

        Text(
            text = row.entry.mark,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.appColors.textTertiary,
        )
    }
}

// Matched to the label's line height rather than to Material's 24dp default: at 24 the icon
// outweighs the word next to it, and this list is read by its words.
private val ICON_SIZE = 20.dp

// The natural height of a row that has a subtitle, applied to every row so a card of sixteen does
// not comb up and down as the content publishes one line here and two there. A minimum rather than
// a fixed height: at the largest accessibility text sizes a two-line row has to be allowed to grow,
// and clipping the subtitle to keep the rhythm would be the wrong trade.
private val ROW_MIN_HEIGHT = 64.dp
