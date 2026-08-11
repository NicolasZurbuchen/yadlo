package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.programme_placeholder_open_happening
import yadlo.shared.generated.resources.tab_programme

// Placeholder. The button is not decoration: it is the only second-level destination in the app
// so far, and it is what exercises per-tab depth and the bottom bar hiding itself on a fiche.
@Composable
fun ProgrammeScreen(
    onHappeningClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md, Alignment.CenterVertically),
        modifier = modifier.fillMaxSize(),
    ) {
        Text(
            text = stringResource(Res.string.tab_programme),
            style = MaterialTheme.typography.headlineMedium,
        )
        Button(onClick = { onHappeningClick(PLACEHOLDER_HAPPENING_ID) }) {
            Text(text = stringResource(Res.string.programme_placeholder_open_happening))
        }
    }
}

// A real id from the published 2026 edition, so the fiche it opens is one that will exist.
private const val PLACEHOLDER_HAPPENING_ID = "dubside"
