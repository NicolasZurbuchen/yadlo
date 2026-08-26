package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.ShimmerPulse
import io.nicolaszurbuchen.yadlo.app.design.theme.shimmerBlock
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing

/**
 * *À essayer* while the Plan and the stands are being read together.
 *
 * *Reversed: this was a centred spinner.* The screen is a two-column staggered grid under Category
 * headings, and both halves of that are worth drawing: the heading, because a grid that appears
 * without one shifts everything down a line when it arrives, and the stagger, because it is the
 * whole visual signature of the screen.
 *
 * **The two columns are deliberately out of step.** A card is a photograph over a name, and the
 * names wrap to one line or two — that is what makes neighbouring cards differ and what staggered
 * means here. Two columns of equal blocks would draw a grid this screen never shows and then jump
 * into the real one.
 */
@Composable
fun WishlistSkeleton(modifier: Modifier = Modifier) {
    ShimmerPulse {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            modifier = modifier.fillMaxWidth().padding(MaterialTheme.spacing.md),
        ) {
            Spacer(modifier = Modifier.width(HEADING_WIDTH).height(HEADING_HEIGHT).shimmerBlock())

            Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
                CardColumn(heights = LEFT_COLUMN, modifier = Modifier.weight(1f))

                CardColumn(heights = RIGHT_COLUMN, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CardColumn(
    heights: List<Int>,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier = modifier,
    ) {
        heights.forEach { height ->
            Spacer(modifier = Modifier.fillMaxWidth().height(height.dp).shimmerBlock())
        }
    }
}

/** A heading is a Category name — *Restauration*, *Créateurs* — so it is short and left-aligned. */
private val HEADING_WIDTH = 132.dp
private val HEADING_HEIGHT = 14.dp

// Card heights rather than a ramp: a StandCard is a photograph over its name, and whether that name
// takes one line or two is what makes two neighbours differ. Taken from the published stands.
private val LEFT_COLUMN = listOf(196, 172, 208)
private val RIGHT_COLUMN = listOf(176, 212, 184)
