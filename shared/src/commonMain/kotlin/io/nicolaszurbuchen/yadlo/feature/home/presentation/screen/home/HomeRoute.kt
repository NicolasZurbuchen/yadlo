package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.uimodel.QuickAccessEntryUiModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeRoute(
    onNavigateToSearch: () -> Unit,
    onNavigateToProgramme: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToQuickAccess: (QuickAccessEntryUiModel) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onNavigateToSearchUpdated by rememberUpdatedState(onNavigateToSearch)
    val onNavigateToProgrammeUpdated by rememberUpdatedState(onNavigateToProgramme)
    val onNavigateToAnnouncementsUpdated by rememberUpdatedState(onNavigateToAnnouncements)
    val onNavigateToQuickAccessUpdated by rememberUpdatedState(onNavigateToQuickAccess)

    // An annonce or a network leaves the app entirely, so it is the platform's business rather than
    // the navigator's — a link out is not a destination and never joins a back stack.
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.labels.collect { label ->
            when (label) {
                HomeLabel.NavigateToSearch -> onNavigateToSearchUpdated()
                HomeLabel.NavigateToProgramme -> onNavigateToProgrammeUpdated()
                HomeLabel.NavigateToAnnouncements -> onNavigateToAnnouncementsUpdated()
                is HomeLabel.OpenUrl -> uriHandler.openUri(label.url)
            }
        }
    }

    HomeScreen(
        state = state,
        onSearchClick = { viewModel.onIntent(HomeIntent.SearchClicked) },
        onHeroClick = { viewModel.onIntent(HomeIntent.HeroClicked) },
        onAnnouncementClick = { url -> viewModel.onIntent(HomeIntent.AnnouncementClicked(url)) },
        onSeeAllAnnouncementsClick = { viewModel.onIntent(HomeIntent.AllAnnouncementsClicked) },
        onSocialClick = { url -> viewModel.onIntent(HomeIntent.SocialClicked(url)) },
        // The same split PlusRoute makes: a tile carrying a url is one that leaves the app, and
        // only it needs the store — the address is on the model the store is already holding.
        // Everything else is a fixed destination, and sending it round the Intent → Executor →
        // Label loop would add a hop that only forwards and put the navigation decision in two
        // places. It is also what keeps a back stack out of a browser.
        //
        // The null branch is the one to watch: an entry given a leaving mark but no url would
        // quietly open a screen instead. `QuickAccessEntryUiModelTest` fails if that happens.
        onQuickAccessClick = { item ->
            if (item.url != null) {
                viewModel.onIntent(HomeIntent.QuickAccessLinkClicked(item.url))
            } else {
                onNavigateToQuickAccessUpdated(item.entry)
            }
        },
        modifier = modifier,
    )
}
