package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.shimmerBlock
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing

/**
 * What *Nourriture & boissons* looks like before the bundle lands: a column of cards.
 *
 * The one Plus screen whose waiting shape is a repeated object rather than prose under headings, so
 * it is the one that could not take [PlusPageSkeleton]. Deliberately a card and not a row of text
 * lines — the real thing arrives as a card, and a placeholder that resolves into a different shape
 * is worse than no placeholder at all.
 *
 * **It is the height of a photograph now**, because the card is. A block a fifth of the real card's
 * height was not a promise about the shape of the answer, it was six small bars where three large
 * ones were about to land.
 */
@Composable
fun StandsSkeleton(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
        modifier = modifier.fillMaxWidth(),
    ) {
        repeat(CARD_COUNT) {
            // One ground under two spacers rather than two blocks: the pulse is a flat alpha, so
            // anything drawn edge to edge in it reads as a single object anyway, and sizing it in
            // two parts is what keeps the picture's proportion honest at any screen width.
            Column(modifier = Modifier.fillMaxWidth().shimmerBlock(MaterialTheme.shapes.small)) {
                Spacer(modifier = Modifier.fillMaxWidth().aspectRatio(IMAGE_RATIO))

                Spacer(modifier = Modifier.height(TEXT_BAND_HEIGHT))
            }
        }
    }
}

/** The card's own frame — see StandCard, where the choice of three by two is argued. */
private const val IMAGE_RATIO = 3f / 2f

// 16dp of padding either side of a title and a body line, which is what nearly every stand
// publishes. Matched by eye to StandCard rather than derived, because it only has to be close
// enough that nothing jumps when the real list arrives.
private val TEXT_BAND_HEIGHT = 92.dp

// Two and a bit fill the shortest phone the app targets, now that a card carries a photograph. A
// skeleton is a promise about shape, not about how many stands there turn out to be.
private const val CARD_COUNT = 3
