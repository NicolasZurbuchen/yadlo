package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.HomeBlockUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * Story 62: on the Monday the app owes a thank-you, not a countdown. It takes the top of Accueil
 * for the six weeks of ENDED, which is the whole point — a countdown to next year would be the app
 * hurrying past a weekend the reader has just lived.
 */
@Composable
fun ThankYouBlock(
    block: HomeBlockUiModel.ThankYou,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.appColors.primarySubtle)
                .padding(MaterialTheme.spacing.lg),
    ) {
        Text(
            text = block.title.asString(),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.appColors.onPrimarySubtle,
        )

        Text(
            text = block.body.asString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.appColors.onPrimarySubtle,
            modifier = Modifier.padding(top = MaterialTheme.spacing.sm),
        )
    }
}
