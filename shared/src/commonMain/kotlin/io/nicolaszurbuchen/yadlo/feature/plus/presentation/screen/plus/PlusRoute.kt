package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PlusRoute(
    onNavigateToEntry: (PlusEntry) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlusViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PlusScreen(
        state = state,
        onEntryClick = onNavigateToEntry,
        modifier = modifier,
    )
}
