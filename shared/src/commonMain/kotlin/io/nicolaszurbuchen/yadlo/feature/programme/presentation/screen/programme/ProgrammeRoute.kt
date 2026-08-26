package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProgrammeRoute(
    onNavigateToHappening: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProgrammeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onNavigateToHappeningUpdated by rememberUpdatedState(onNavigateToHappening)

    LaunchedEffect(Unit) {
        viewModel.labels.collect { label ->
            when (label) {
                is ProgrammeLabel.NavigateToHappening -> onNavigateToHappeningUpdated(label.happeningId)
            }
        }
    }

    ProgrammeScreen(
        state = state,
        onScopeClick = { scopeId -> viewModel.onIntent(ProgrammeIntent.ScopeSelected(scopeId)) },
        onCategoryClick = { categoryId -> viewModel.onIntent(ProgrammeIntent.CategoryToggled(categoryId)) },
        onAllCategoriesClick = { viewModel.onIntent(ProgrammeIntent.AllCategoriesSelected) },
        onSlotClick = { happeningId -> viewModel.onIntent(ProgrammeIntent.SlotClicked(happeningId)) },
        modifier = modifier,
    )
}
