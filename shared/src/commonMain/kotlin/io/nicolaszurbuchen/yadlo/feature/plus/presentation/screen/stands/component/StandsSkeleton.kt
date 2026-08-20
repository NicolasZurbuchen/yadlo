package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
 * What *Nourriture & boissons* looks like before the bundle lands: a two-column grid of cards.
 *
 * The one Plus screen whose waiting shape is a repeated object rather than prose under headings, so
 * it is the one that could not take [PlusPageSkeleton]. Deliberately the card's own shape and the
 * card's own grid — the real thing arrives as two columns of photographs, and a placeholder that
 * resolves into a different arrangement is worse than no placeholder at all.
 */
@Composable
fun StandsSkeleton(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier = modifier.fillMaxWidth(),
    ) {
        repeat(ROW_COUNT) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                modifier = Modifier.fillMaxWidth(),
            ) {
                repeat(COLUMNS) {
                    // One ground under two spacers rather than two blocks: the pulse is a flat
                    // alpha, so anything drawn edge to edge in it reads as a single object anyway,
                    // and sizing it in two parts is what keeps the picture's proportion honest at
                    // any screen width.
                    Column(modifier = Modifier.weight(1f).shimmerBlock(MaterialTheme.shapes.small)) {
                        Spacer(modifier = Modifier.fillMaxWidth().aspectRatio(IMAGE_RATIO))

                        Spacer(modifier = Modifier.height(TEXT_BAND_HEIGHT))
                    }
                }
            }
        }
    }
}

/** The card's own frame — see StandCard, where the choice of three by two is argued. */
private const val IMAGE_RATIO = 3f / 2f

/** And the card's own grid — see StandsScreen. */
private const val COLUMNS = 2

// A name and an offering line at half a phone's width, which is two lines each more often than one,
// plus 16dp of padding around them. Matched by eye to StandCard rather than derived, because it only
// has to be close enough that nothing jumps when the real grid arrives.
private val TEXT_BAND_HEIGHT = 104.dp

// Three rows of two fills the shortest phone the app targets and no more. A skeleton is a promise
// about shape, not about how many stands there turn out to be.
private const val ROW_COUNT = 3
