package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ContactRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContactViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.labels.collect { label ->
            when (label) {
                is ContactLabel.OpenUrl -> uriHandler.openUri(label.url)
            }
        }
    }

    ContactScreen(
        state = state,
        onBackClick = onNavigateBack,
        onEmailClick = { address -> viewModel.onIntent(ContactIntent.EmailClicked(address)) },
        modifier = modifier,
    )
}
