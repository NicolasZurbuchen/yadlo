package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AccessRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AccessViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.labels.collect { label ->
            when (label) {
                is AccessLabel.OpenUrl -> uriHandler.openUri(label.url)
            }
        }
    }

    AccessScreen(
        state = state,
        onBackClick = onNavigateBack,
        onLinkClick = { url -> viewModel.onIntent(AccessIntent.LinkClicked(url)) },
        modifier = modifier,
    )
}
