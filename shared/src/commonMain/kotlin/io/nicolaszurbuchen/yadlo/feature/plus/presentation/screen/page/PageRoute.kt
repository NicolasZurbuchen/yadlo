package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/**
 * Which page this is reaches the store through the ViewModel, never through here: a Route may only
 * take lambdas, a Modifier or a ViewModel. Same route the fiche's Happening id travels.
 */
@Composable
fun PageRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PageViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.labels.collect { label ->
            when (label) {
                is PageLabel.OpenUrl -> uriHandler.openUri(label.url)
            }
        }
    }

    PageScreen(
        state = state,
        onBackClick = onNavigateBack,
        onLinkClick = { url -> viewModel.onIntent(PageIntent.LinkClicked(url)) },
        modifier = modifier,
    )
}
