package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PlusRoute(
    onNavigateToEntry: (PlusEntry) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlusViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.labels.collect { label ->
            when (label) {
                is PlusLabel.OpenUrl -> uriHandler.openUri(label.url)
            }
        }
    }

    PlusScreen(
        state = state,
        // Two of the nineteen rows leave the app rather than opening a screen, and the row itself
        // says which: `↗` and `✉` against the chevron everything else wears. Splitting them here
        // rather than in the navigator keeps the app's back stack out of a mail client.
        onEntryClick = { entry ->
            when (entry) {
                PlusEntry.NEWSLETTER -> viewModel.onIntent(PlusIntent.NewsletterClicked)
                PlusEntry.REPORT -> viewModel.onIntent(PlusIntent.ReportClicked)
                else -> onNavigateToEntry(entry)
            }
        },
        modifier = modifier,
    )
}
