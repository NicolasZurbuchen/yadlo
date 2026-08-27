package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.shimmerBlock
import io.nicolaszurbuchen.yadlo.design.theme.spacing

/**
 * *Nous écrire* while it is arriving: a column of tiles, then the postal address.
 *
 * Its own rather than the default page skeleton because this screen is not prose. It is nine tiles
 * of near-identical height, which is a strong enough silhouette to be recognisable before a word of
 * it is legible — and a placeholder of paragraphs would settle the eye on a page that is about to
 * become a list.
 */
@Composable
fun ContactSkeleton(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Spacer(modifier = Modifier.width(HEADER_WIDTH).height(HEADER_HEIGHT).shimmerBlock())

            repeat(TILE_COUNT) { TileSkeleton() }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Spacer(modifier = Modifier.width(HEADER_WIDTH).height(HEADER_HEIGHT).shimmerBlock())

            ADDRESS_LINES.forEach { fraction ->
                Spacer(modifier = Modifier.fillMaxWidth(fraction).height(LINE_HEIGHT).shimmerBlock())
            }
        }
    }
}

/** A tile with a label and an address under it, at the height YadloLinkTile settles at. */
@Composable
private fun TileSkeleton(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs, Alignment.CenterVertically),
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.appColors.surface)
                .heightIn(min = TILE_MIN_HEIGHT)
                .padding(MaterialTheme.spacing.md),
    ) {
        Spacer(modifier = Modifier.width(LABEL_WIDTH).height(LINE_HEIGHT).shimmerBlock())

        Spacer(modifier = Modifier.width(SUBLABEL_WIDTH).height(SUBLINE_HEIGHT).shimmerBlock())
    }
}

// The published directory is nine addresses; drawing all nine means the column does not visibly
// grow when they land.
private const val TILE_COUNT = 9

private val ADDRESS_LINES = listOf(0.5f, 0.4f, 0.25f)

private val HEADER_WIDTH = 128.dp
private val HEADER_HEIGHT = 12.dp
private val LINE_HEIGHT = 16.dp
private val SUBLINE_HEIGHT = 12.dp
private val LABEL_WIDTH = 176.dp
private val SUBLABEL_WIDTH = 144.dp

// Matched to YadloLinkTile.
private val TILE_MIN_HEIGHT = 64.dp
