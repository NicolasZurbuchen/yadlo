package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloLinkMarkUiModel
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.HomeBlockUiModel
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.QuickAccessItemUiModel
import org.jetbrains.compose.resources.stringResource

/**
 * The Phase's shortlist, as a row of tiles under a heading that says why these and why now.
 *
 * **Tiles across rather than rows down, and the difference is the whole argument.** A stack of
 * full-width rows is exactly what the Plus tab looks like, and a block on Accueil that reads as a
 * shorter Plus invites the reader to treat it as one — a list to scan past. Drawn across, the two
 * or three of them read as what they are: a small number of things raised on purpose.
 *
 * The heading and the border come from [SectionBlock], shared with the annonces and the chiffres,
 * so the block sits among its siblings rather than arriving as a fourth person's idea of a section.
 *
 * They share a height through [IntrinsicSize.Min] rather than a fixed one, because the labels wrap
 * differently — *Accès & transports* takes two lines where *Newsletter* takes one — and tiles of
 * visibly different heights would read as different kinds of thing. A minimum keeps the largest
 * accessibility text sizes able to grow them all together.
 */
@Composable
fun QuickAccessBlock(
    block: HomeBlockUiModel.QuickAccess,
    onItemClick: (QuickAccessItemUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionBlock(title = block.title, modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        ) {
            block.items.forEach { item ->
                QuickAccessTile(
                    item = item,
                    onClick = onItemClick,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
            }
        }
    }
}

@Composable
private fun QuickAccessTile(
    item: QuickAccessItemUiModel,
    onClick: (QuickAccessItemUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = stringResource(item.entry.title)

    Box(
        modifier =
            modifier
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.appColors.surface)
                .clickable { onClick(item) }
                .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.md),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
        ) {
            Icon(
                imageVector = item.entry.icon,
                // The label under it is the same word, so announcing the glyph would say it twice.
                contentDescription = null,
                tint = MaterialTheme.appColors.primary,
                modifier = Modifier.size(ICON_SIZE),
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.appColors.textPrimary,
                textAlign = TextAlign.Center,
            )
        }

        // **Only the leaving mark is drawn, and only in the corner.** A tile has no trailing column
        // to hang a chevron off, and the chevron was already the mark that says nothing the tap has
        // not said — YadloLinkMarkUiModel gives it no description for exactly that reason. The one
        // fact a tile cannot convey on its own is that tapping it costs a page load, so that is the
        // one that gets a glyph. Its absence is what says the tile stays in the app.
        if (item.entry.mark != YadloLinkMarkUiModel.DISCLOSURE) {
            Icon(
                imageVector = item.entry.mark.icon,
                contentDescription = item.entry.mark.contentDescription?.let { stringResource(it) },
                tint = MaterialTheme.appColors.textTertiary,
                modifier = Modifier.size(MARK_SIZE).align(Alignment.TopEnd),
            )
        }
    }
}

/**
 * The subject of the tile, so larger than the 24dp of an icon that merely labels a row — but not so
 * large it competes with the word under it, which is the thing actually being read.
 */
private val ICON_SIZE = 28.dp

/** A footnote on the tile rather than a second subject, so smaller than either icon in the app. */
private val MARK_SIZE = 14.dp
