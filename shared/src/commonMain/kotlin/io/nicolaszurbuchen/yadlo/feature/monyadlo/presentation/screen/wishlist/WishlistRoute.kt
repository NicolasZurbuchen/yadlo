package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WishlistRoute(
    onNavigateBack: () -> Unit,
    onNavigateToHappening: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WishlistViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    WishlistScreen(
        state = state,
        onBackClick = onNavigateBack,
        onStandClick = onNavigateToHappening,
        modifier = modifier,
    )
}
