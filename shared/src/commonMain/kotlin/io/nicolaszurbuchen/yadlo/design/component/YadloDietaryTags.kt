package io.nicolaszurbuchen.yadlo.design.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.design.theme.spacing
import io.nicolaszurbuchen.yadlo.design.uimodel.YadloDietaryTagUiModel
import org.jetbrains.compose.resources.stringResource

/**
 * What can be eaten here, said in glyphs and in words.
 *
 * On a stand row it is what the whole truck can feed you, on a dish it is what that dish is — the
 * wording is decided upstream, see [YadloDietaryTagUiModel]. One component either way, because the two
 * appear one screen apart and a reader with a dietary requirement is following the same thread
 * through both.
 *
 * **Never the glyph alone.** Every tag is written out beside its mark. These are read by someone who
 * cannot eat what they get wrong, which is not a place to make a reader learn six icons — and it is
 * also what keeps them legible to a colour-blind reader and to a screen reader, so neither needs a
 * content description repeating the label two words later.
 *
 * Wrapping rather than scrolling, because a tag hidden off the right edge is a tag that was not
 * said: a dish can carry four of these, and *sans gluten* is exactly the one that would fall off.
 */
@Composable
fun YadloDietaryTags(
    tags: List<YadloDietaryTagUiModel>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
        modifier = modifier.fillMaxWidth(),
    ) {
        tags.forEach { tag ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = tag.mark.icon,
                    // The label says it. A description here has a screen reader announce every tag
                    // twice, which on a dish carrying four of them is eight words for four facts.
                    contentDescription = null,
                    tint = tag.mark.tint,
                    modifier = Modifier.size(GLYPH_SIZE),
                )

                Text(
                    text = stringResource(tag.label),
                    style = MaterialTheme.typography.labelSmall,
                    color = tag.mark.tint,
                )
            }
        }
    }
}

// Matched to the label's own line height rather than Material's 24dp default: the glyph qualifies
// the word beside it and should not outweigh it.
private val GLYPH_SIZE = 14.dp
