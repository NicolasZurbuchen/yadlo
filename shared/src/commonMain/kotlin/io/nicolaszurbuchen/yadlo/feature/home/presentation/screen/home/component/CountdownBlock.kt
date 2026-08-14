package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.HomeBlockUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * The reason to keep the app installed on the 361 days when nothing is happening (story 56).
 *
 * Flat rather than carded, and left-aligned: it is the first thing on the screen, so it is the
 * heading of the page rather than one tile among several.
 */
@Composable
fun CountdownBlock(
    block: HomeBlockUiModel.Countdown,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = block.daysText.asString(),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.appColors.textPrimary,
        )

        Text(
            text = block.subtitle,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.appColors.textSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
