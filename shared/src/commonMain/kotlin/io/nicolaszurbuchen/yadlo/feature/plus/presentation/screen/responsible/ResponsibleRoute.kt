package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.responsible

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ResponsibleRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ResponsibleViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.labels.collect { label ->
            when (label) {
                is ResponsibleLabel.OpenUrl -> uriHandler.openUri(label.url)
            }
        }
    }

    ResponsibleScreen(
        state = state,
        onBackClick = onNavigateBack,
        onLinkClick = { url -> viewModel.onIntent(ResponsibleIntent.LinkClicked(url)) },
        modifier = modifier,
    )
}
