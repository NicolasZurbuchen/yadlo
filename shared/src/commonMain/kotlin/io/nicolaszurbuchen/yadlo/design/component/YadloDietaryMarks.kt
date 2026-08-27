package io.nicolaszurbuchen.yadlo.design.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.design.theme.spacing
import io.nicolaszurbuchen.yadlo.design.uimodel.YadloDietaryTagUiModel
import org.jetbrains.compose.resources.stringResource

/**
 * The same six marks as [YadloDietaryTags], written as glyphs alone — for the one place a carte is
 * read rather than a card: beside a dish name.
 *
 * **Where the words went.** They are at the top of the fiche, once, as [YadloDietaryTags]: *100 %
 * végan*, *options sans gluten*. Everything a stand's carte can carry is named there before a reader
 * reaches the first dish, so the glyph below has a legend rather than being a symbol to guess at.
 *
 * That is the trade, and it is a real one. On a carte of fourteen dishes the words were the layout:
 * a dish carrying four marks spent two lines on *Végan · Sans lactose · Sans gluten · Piquant*
 * under a name and a price that took one, so the marks outweighed the food. Repeating the whole
 * vocabulary fourteen times is not what makes it safe to act on — saying it once, in full, above
 * the list is.
 *
 * **The word is still there for anyone who is not reading the picture.** Each glyph carries its own
 * label as its content description, which is the opposite of [YadloDietaryTags], where a description
 * would have a screen reader announce every tag twice. Here it is the only place the word exists on
 * the row.
 *
 * A [Row] rather than a wrapping FlowRow: this sits inline after a dish name, where a second line of
 * glyphs would push the price off its own line. Four is the most any dish in the 2026 content
 * carries, which fits beside all but the longest names.
 */
@Composable
fun YadloDietaryMarks(
    tags: List<YadloDietaryTagUiModel>,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        tags.forEach { tag ->
            Icon(
                imageVector = tag.mark.icon,
                contentDescription = stringResource(tag.label),
                tint = tag.mark.tint,
                modifier = Modifier.size(GLYPH_SIZE),
            )
        }
    }
}

// A step larger than the 14dp a tag's glyph takes beside its own word. There is no word here to be
// measured against, and at 14dp a lone leaf next to a 16sp dish name read as a speck rather than as
// a mark.
private val GLYPH_SIZE = 16.dp
