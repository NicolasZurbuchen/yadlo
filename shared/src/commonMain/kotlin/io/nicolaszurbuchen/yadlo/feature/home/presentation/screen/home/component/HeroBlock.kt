package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
 * The one block on Accueil with a job other than being read: it sends the visitor to the programme.
 *
 * The whole card is the target rather than a button inside it, which is what the prototype shows
 * and what the rest of the app does — a row you can read is a row you can tap.
 */
@Composable
fun HeroBlock(
    block: HomeBlockUiModel.Hero,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.appColors.accentSubtle)
                .clickable(onClick = onClick)
                .padding(MaterialTheme.spacing.md),
    ) {
        Text(
            text = block.kicker.asString(),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.appColors.onAccentSubtle,
        )

        Text(
            text = block.title.asString(),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.appColors.onAccentSubtle,
            modifier = Modifier.padding(top = MaterialTheme.spacing.xs),
        )

        Text(
            text = block.body.asString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.appColors.onAccentSubtle,
            modifier = Modifier.padding(top = MaterialTheme.spacing.xs),
        )
    }
}
