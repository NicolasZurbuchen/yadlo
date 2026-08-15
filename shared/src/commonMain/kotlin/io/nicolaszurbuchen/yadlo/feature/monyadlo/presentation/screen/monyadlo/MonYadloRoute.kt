package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MonYadloRoute(
    onNavigateToHappening: (String) -> Unit,
    onNavigateToWishlist: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MonYadloViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MonYadloScreen(
        state = state,
        onSlotClick = onNavigateToHappening,
        onWishlistClick = onNavigateToWishlist,
        modifier = modifier,
    )
}
