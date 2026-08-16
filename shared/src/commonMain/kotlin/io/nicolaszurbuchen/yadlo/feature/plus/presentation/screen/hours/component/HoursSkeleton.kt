package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.shimmerBlock
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing

/**
 * *Horaires* while it is arriving: a line of intro, then one card per day.
 *
 * Three cards, because the edition is three days and the skeleton should not visibly grow into the
 * answer. This screen is the strongest case in the tab for drawing the shape rather than a spinner:
 * three identical cards is a silhouette anyone recognises before a digit of it is legible.
 */
@Composable
fun HoursSkeleton(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Spacer(modifier = Modifier.fillMaxWidth().height(LINE_HEIGHT).shimmerBlock())

            Spacer(modifier = Modifier.fillMaxWidth(INTRO_LAST_LINE).height(LINE_HEIGHT).shimmerBlock())
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            modifier = Modifier.fillMaxWidth(),
        ) {
            repeat(DAY_COUNT) { DayCardSkeleton() }
        }
    }
}

@Composable
private fun DayCardSkeleton(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.appColors.surface)
                .padding(MaterialTheme.spacing.md),
    ) {
        Spacer(modifier = Modifier.width(NAME_WIDTH).height(NAME_HEIGHT).shimmerBlock())

        Spacer(modifier = Modifier.width(WINDOW_WIDTH).height(WINDOW_HEIGHT).shimmerBlock())
    }
}

// Vendredi, samedi, dimanche.
private const val DAY_COUNT = 3

// The intro runs to a line and a half, and a placeholder that squared it off would read as a table.
private const val INTRO_LAST_LINE = 0.55f

private val LINE_HEIGHT = 16.dp
private val NAME_WIDTH = 72.dp
private val NAME_HEIGHT = 12.dp

// Sized to "16:00 - 02:00" at the heading face the real window is set in.
private val WINDOW_WIDTH = 144.dp
private val WINDOW_HEIGHT = 24.dp
