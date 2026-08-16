package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AssistanceRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AssistanceViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // A dialer and a mail app are both the platform's business rather than the navigator's — the
    // same seam every external link in this app goes through.
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.labels.collect { label ->
            when (label) {
                is AssistanceLabel.OpenUrl -> uriHandler.openUri(label.url)
            }
        }
    }

    AssistanceScreen(
        state = state,
        onBackClick = onNavigateBack,
        onNumberClick = { number -> viewModel.onIntent(AssistanceIntent.NumberClicked(number)) },
        onLostPropertyClick = { email -> viewModel.onIntent(AssistanceIntent.LostPropertyClicked(email)) },
        modifier = modifier,
    )
}
