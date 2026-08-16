package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
 */
@Composable
fun StandsSkeleton(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier = modifier.fillMaxWidth(),
    ) {
        repeat(CARD_COUNT) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(CARD_HEIGHT)
                        .shimmerBlock(MaterialTheme.shapes.small),
            )
        }
    }
}

// A stand card carrying a name and an offering line, which is what nearly all of them publish:
// 16dp of padding either side of a title and a body line. Matched by eye to StandRow rather than
// derived, because it only has to be close enough that nothing jumps when the real list arrives.
private val CARD_HEIGHT = 76.dp

// Six fills the shortest phone the app targets and no more. A skeleton is a promise about shape,
// not about how many stands there turn out to be.
private const val CARD_COUNT = 6
