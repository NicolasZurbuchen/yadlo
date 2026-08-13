package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
 * The one block on Accueil with a job other than being read: ANNOUNCED sends the visitor to the
 * programme, APPROACHING sends them to their Plan. Which of the two it is has already been decided
 * by the time it renders — the button carries a label, not a destination.
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
                .padding(MaterialTheme.spacing.md),
    ) {
        Text(
            text = block.title.asString(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.appColors.onAccentSubtle,
        )

        Text(
            text = block.body.asString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.appColors.onAccentSubtle,
            modifier = Modifier.padding(top = MaterialTheme.spacing.xs),
        )

        Button(
            onClick = onClick,
            modifier = Modifier.padding(top = MaterialTheme.spacing.md),
        ) {
            Text(text = block.actionLabel.asString())
        }
    }
}
