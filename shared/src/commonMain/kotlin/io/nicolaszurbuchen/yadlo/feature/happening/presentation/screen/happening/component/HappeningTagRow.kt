package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.spacing

/**
 * Attributes only — genres for an artist, the offering and the marks for a stand.
 *
 * Never the Category, which is already written above the title, and never the venue, which the date
 * rows and the map say. A tag that repeats what is two lines above it teaches the reader that tags
 * are decoration.
 *
 * Deliberately not tappable and deliberately not a filter chip: these are facts about one Happening,
 * and the filter chips they would otherwise resemble live on the Programme and *do* something.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HappeningTagRow(
    tags: List<String>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier = modifier.fillMaxWidth(),
    ) {
        tags.forEach { tag ->
            Text(
                text = tag.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.appColors.textSecondary,
                modifier =
                    Modifier
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.appColors.surfaceRaised)
                        .padding(horizontal = MaterialTheme.spacing.sm, vertical = TAG_VERTICAL_PADDING),
            )
        }
    }
}

/** Below the smallest spacing step, for the same reason as the live-state pill: a tag is not a button. */
private val TAG_VERTICAL_PADDING = 4.dp
