package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/**
 * Which half of the stands this is reaches the store through the ViewModel, never through here: a
 * Route may only take lambdas, a Modifier or a ViewModel.
 */
@Composable
fun StandsRoute(
    onNavigateBack: () -> Unit,
    onNavigateToHappening: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StandsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onNavigateToHappeningUpdated by rememberUpdatedState(onNavigateToHappening)

    LaunchedEffect(Unit) {
        viewModel.labels.collect { label ->
            when (label) {
                is StandsLabel.NavigateToHappening -> onNavigateToHappeningUpdated(label.happeningId)
            }
        }
    }

    StandsScreen(
        state = state,
        onBackClick = onNavigateBack,
        onMarkClick = { mark -> viewModel.onIntent(StandsIntent.MarkSelected(mark)) },
        onStandClick = { id -> viewModel.onIntent(StandsIntent.StandClicked(id)) },
        modifier = modifier,
    )
}
