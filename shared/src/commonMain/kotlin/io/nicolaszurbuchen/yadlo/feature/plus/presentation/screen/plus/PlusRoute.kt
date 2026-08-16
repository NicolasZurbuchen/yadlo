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
    onNavigateToEntry: (PlusEntryUiModel) -> Unit,
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
        // **A row goes through the store only when the store knows something the tap does not.**
        // Newsletter and Signaler are the two: neither carries its address, and the address is on
        // the overview the store is already holding. Every other row is a fixed destination, and
        // sending it round the Intent → Executor → Label loop would add a hop that only forwards —
        // one more frame before the push, a Label that can outlive the screen that published it,
        // and the navigation decision written down in two places instead of one. Keeping the split
        // here rather than in the navigator is also what keeps the app's back stack out of a mail
        // client.
        //
        // The `else` is the branch to watch: a row added later with a leaving mark and no case here
        // would quietly open a screen instead. `PlusEntryUiModelTest` fails if that happens.
        onEntryClick = { entry ->
            when (entry) {
                PlusEntryUiModel.NEWSLETTER -> viewModel.onIntent(PlusIntent.NewsletterClicked)
                PlusEntryUiModel.REPORT -> viewModel.onIntent(PlusIntent.ReportClicked)
                else -> onNavigateToEntry(entry)
            }
        },
        // The networks do carry their own URL, so this one could have gone straight to the
        // UriHandler. It goes through the store anyway because opening one is an act on the
        // festival's behalf rather than navigation, and Accueil routes the identical tap the same
        // way.
        onSocialClick = { url -> viewModel.onIntent(PlusIntent.SocialClicked(url)) },
        modifier = modifier,
    )
}
