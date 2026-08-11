package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.tab_plus

// Placeholder. Replaced when Plus is built.
@Composable
fun PlusScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize(),
    ) {
        Text(
            text = stringResource(Res.string.tab_plus),
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}
