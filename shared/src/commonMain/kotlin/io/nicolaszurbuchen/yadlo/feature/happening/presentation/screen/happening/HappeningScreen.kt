package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.happening_placeholder_back
import yadlo.shared.generated.resources.happening_placeholder_title

// Placeholder for the fiche. The back action is real: this is the screen that proves the bottom
// bar hides at depth and that back returns to the tab the fiche was opened from.
@Composable
fun HappeningScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md, Alignment.CenterVertically),
        modifier = modifier.fillMaxSize(),
    ) {
        Text(
            text = stringResource(Res.string.happening_placeholder_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        TextButton(onClick = onBackClick) {
            Text(text = stringResource(Res.string.happening_placeholder_back))
        }
    }
}
