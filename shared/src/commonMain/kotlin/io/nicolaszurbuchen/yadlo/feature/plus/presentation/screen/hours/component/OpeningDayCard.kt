package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours.OpeningDayUiModel

/**
 * One day, and the one time that matters.
 *
 * The card used to carry the programme window under the opening one, which made the reader sort two
 * times to find the one they came for. There is a single answer on it now, set in the display face,
 * and the day's name above it in the supporting role.
 */
@Composable
fun OpeningDayCard(
    day: OpeningDayUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.appColors.surface)
                .padding(MaterialTheme.spacing.md),
    ) {
        Text(
            text = day.name.uppercase(),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.appColors.textTertiary,
        )

        Text(
            text = day.window,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.appColors.textPrimary,
        )
    }
}
