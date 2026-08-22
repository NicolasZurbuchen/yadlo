package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.nicolaszurbuchen.yadlo.infra.platform.rememberShareLauncher
import io.nicolaszurbuchen.yadlo.infra.ui.asString
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HappeningRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HappeningViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // A booking page or an artist's own site leaves the app entirely, so it is the platform's
    // business rather than the navigator's — the same reasoning as the annonces on Accueil.
    val uriHandler = LocalUriHandler.current

    // Nothing to remember and no state to change, so it never reaches the store — the text is
    // already on the model and the sheet is the platform’s, exactly like opening a url.
    val shareLauncher = rememberShareLauncher()

    // Resolved here rather than in the mapper: the opening sentence is a resource, and only a
    // composition can read one. Everything else in the message is already assembled.
    val shareText = state.shareText?.asString()

    LaunchedEffect(Unit) {
        viewModel.labels.collect { label ->
            when (label) {
                is HappeningLabel.OpenUrl -> uriHandler.openUri(label.url)
            }
        }
    }

    HappeningScreen(
        state = state,
        onBackClick = onNavigateBack,
        onLinkClick = { url -> viewModel.onIntent(HappeningIntent.LinkClicked(url)) },
        onSlotHeartClick = { slotId -> viewModel.onIntent(HappeningIntent.SlotHeartClicked(slotId)) },
        onWishlistHeartClick = { viewModel.onIntent(HappeningIntent.WishlistHeartClicked) },
        onShareClick = { shareText?.let(shareLauncher::share) },
        modifier = modifier,
    )
}
