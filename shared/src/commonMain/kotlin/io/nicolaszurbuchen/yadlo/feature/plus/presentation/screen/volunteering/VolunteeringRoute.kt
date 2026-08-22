package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.nicolaszurbuchen.yadlo.infra.platform.rememberShareLauncher
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun VolunteeringRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VolunteeringViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // The sheet is the platform’s and there is no state to change, so this never reaches the store.
    val shareLauncher = rememberShareLauncher()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.labels.collect { label ->
            when (label) {
                is VolunteeringLabel.OpenUrl -> uriHandler.openUri(label.url)
            }
        }
    }

    VolunteeringScreen(
        state = state,
        onBackClick = onNavigateBack,
        onSignupClick = { url -> viewModel.onIntent(VolunteeringIntent.SignupClicked(url)) },
        onEmailClick = { address -> viewModel.onIntent(VolunteeringIntent.EmailClicked(address)) },
        onShareClick = { state.shareText?.let(shareLauncher::share) },
        modifier = modifier,
    )
}
