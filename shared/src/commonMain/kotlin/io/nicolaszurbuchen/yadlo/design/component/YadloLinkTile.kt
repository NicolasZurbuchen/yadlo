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
import io.nicolaszurbuchen.yadlo.design.uimodel.YadloLinkMarkUiModel
import org.jetbrains.compose.resources.stringResource

/**
 * A tappable destination on a page — the TWINT site, a timetable PDF, an address to write to.
 *
 * In the design system rather than in `feature/plus/`: an artist's own site on the fiche and a
 * partner's on Accueil are the same row with different words in it, and a component three features
 * draw belongs in `app/` by the rule in CLAUDE.md.
 *
 * **The trailing mark says where the tap goes**, which is the whole reason facts and links look
 * different. On a beach with one bar of signal that difference is what tells someone whether tapping
 * is about to cost them a page load — see [YadloLinkMarkUiModel].
 *
 * [sublabel] is where the label alone would be a guess — *PDF · MBC* under a line number, the
 * address under *Écrivez-nous*.
 *
 * It is deliberately as tall as a row of the Plus tab. These tiles and that list are the two things
 * the app asks a wet thumb to hit, and the tile used to be a third shorter than the row for no
 * reason other than that it was written second.
 */
@Composable
fun YadloLinkTile(
    label: String,
    mark: YadloLinkMarkUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sublabel: String? = null,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.appColors.surface)
                .clickable { onClick() }
                .heightIn(min = TILE_MIN_HEIGHT)
                .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.md),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.appColors.textPrimary,
            )

            sublabel?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.appColors.textSecondary,
                )
            }
        }

        Icon(
            imageVector = mark.icon,
            contentDescription = mark.contentDescription?.let { stringResource(it) },
            tint = MaterialTheme.appColors.textTertiary,
            modifier = Modifier.size(MARK_SIZE),
        )
    }
}

// The trailing mark reads as punctuation on the label, not as a second subject: sized to the
// label's own line height rather than to Material's 24dp default, which would make it compete with
// the leading icon on the rows that have one.
private val MARK_SIZE = 20.dp

// The same minimum a Plus row takes, and for the same reason: a tile with a sublabel and one
// without should not be visibly different objects, and a column of them should not comb up and down
// as the content publishes a second line here and not there. A minimum rather than a fixed height,
// so the largest accessibility text sizes can still grow it.
private val TILE_MIN_HEIGHT = 64.dp
