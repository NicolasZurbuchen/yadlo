package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StoryRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StoryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    StoryScreen(
        state = state,
        onBackClick = onNavigateBack,
        modifier = modifier,
    )
}
