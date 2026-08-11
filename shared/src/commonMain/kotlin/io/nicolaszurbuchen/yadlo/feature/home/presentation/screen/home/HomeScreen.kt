package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.tab_home

// Placeholder. Replaced when the Accueil feature is built; it exists now so the tab shell has a
// real destination to render and the navigation can be exercised end to end.
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize(),
    ) {
        Text(
            text = stringResource(Res.string.tab_home),
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}
