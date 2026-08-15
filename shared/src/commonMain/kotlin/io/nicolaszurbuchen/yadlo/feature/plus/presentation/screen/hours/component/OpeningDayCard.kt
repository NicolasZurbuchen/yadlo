package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours.OpeningDayUiModel
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.hours_programme

/**
 * One day, with the answer set large.
 *
 * **Only one time on this card really matters** — when the site opens and closes — so it takes the
 * display face at heading size and the programme window sits under it in the supporting role. A
 * card that gave both the same weight would make the reader do the sorting the screen exists to
 * have done for them.
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

        day.programme?.let {
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(Res.string.hours_programme),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.appColors.textSecondary,
                    modifier = Modifier.weight(1f),
                )

                Text(
                    text = it,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.appColors.textSecondary,
                )
            }
        }
    }
}
