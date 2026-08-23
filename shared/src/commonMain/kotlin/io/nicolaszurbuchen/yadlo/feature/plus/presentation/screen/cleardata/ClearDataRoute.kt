package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.cleardata

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClearDataRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClearDataViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // No label collector: ClearDataLabel has no members. Both buttons finish where they were
    // pressed, which is the reason this screen has nothing to signal.
    ClearDataScreen(
        state = state,
        onBackClick = onNavigateBack,
        onSavedClick = { viewModel.onIntent(ClearDataIntent.SavedClicked) },
        onSavedConfirm = { viewModel.onIntent(ClearDataIntent.SavedConfirmed) },
        onSavedDismiss = { viewModel.onIntent(ClearDataIntent.SavedDismissed) },
        onImagesClick = { viewModel.onIntent(ClearDataIntent.ImagesClicked) },
        modifier = modifier,
    )
}
