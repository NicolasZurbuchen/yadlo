package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.shimmerBlock
import io.nicolaszurbuchen.yadlo.app.design.theme.sizing
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing

/**
 * *Devenir Hot'Staff* while it is arriving: the ask, the three perks, and the two ways to act.
 *
 * Its own rather than the default page skeleton because the screen ends in tiles rather than in
 * prose, and those two tiles are the whole point of opening it. A placeholder that put paragraphs
 * where the buttons go would move the one thing the reader came for.
 */
@Composable
fun VolunteeringSkeleton(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Spacer(modifier = Modifier.width(HEADER_WIDTH).height(HEADER_HEIGHT).shimmerBlock())

            BODY_LINES.forEach { fraction ->
                Spacer(modifier = Modifier.fillMaxWidth(fraction).height(LINE_HEIGHT).shimmerBlock())
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Spacer(modifier = Modifier.width(HEADER_WIDTH).height(HEADER_HEIGHT).shimmerBlock())

            PERK_LINES.forEach { fraction -> PerkSkeleton(widthFraction = fraction) }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Spacer(modifier = Modifier.width(HEADER_WIDTH).height(HEADER_HEIGHT).shimmerBlock())

            repeat(TILE_COUNT) { TileSkeleton() }
        }
    }
}

/** A mark and the fact it qualifies, at the geometry YadloFactRow lays out. */
@Composable
private fun PerkSkeleton(
    widthFraction: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.size(MaterialTheme.sizing.icon).shimmerBlock())

        Spacer(modifier = Modifier.fillMaxWidth(widthFraction).height(LINE_HEIGHT).shimmerBlock())
    }
}

@Composable
private fun TileSkeleton(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.appColors.surface)
                .heightIn(min = MaterialTheme.sizing.rowMinHeight)
                .padding(MaterialTheme.spacing.md),
    ) {
        Spacer(modifier = Modifier.width(TILE_LABEL_WIDTH).height(LINE_HEIGHT).shimmerBlock())
    }
}

// Six hours minimum runs to three lines on the published page; the perks are three short ones.
private val BODY_LINES = listOf(1f, 1f, 0.72f)
private val PERK_LINES = listOf(0.62f, 0.86f, 0.5f)

// S'inscrire and the staff address.
private const val TILE_COUNT = 2

private val HEADER_WIDTH = 112.dp
private val HEADER_HEIGHT = 12.dp
private val LINE_HEIGHT = 16.dp
private val TILE_LABEL_WIDTH = 136.dp
