package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.tab_mon_yadlo

// Placeholder. Replaced when Mon Yadlo is built.
@Composable
fun MonYadloScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize(),
    ) {
        Text(
            text = stringResource(Res.string.tab_mon_yadlo),
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}
