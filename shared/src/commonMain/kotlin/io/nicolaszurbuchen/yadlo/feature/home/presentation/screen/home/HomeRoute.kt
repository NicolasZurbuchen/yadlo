package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeRoute(
    onNavigateToProgramme: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onNavigateToProgrammeUpdated by rememberUpdatedState(onNavigateToProgramme)
    val onNavigateToAnnouncementsUpdated by rememberUpdatedState(onNavigateToAnnouncements)

    // An annonce or a network leaves the app entirely, so it is the platform's business rather than
    // the navigator's — a link out is not a destination and never joins a back stack.
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.labels.collect { label ->
            when (label) {
                HomeLabel.NavigateToProgramme -> onNavigateToProgrammeUpdated()
                HomeLabel.NavigateToAnnouncements -> onNavigateToAnnouncementsUpdated()
                is HomeLabel.OpenUrl -> uriHandler.openUri(label.url)
            }
        }
    }

    HomeScreen(
        state = state,
        onHeroClick = { viewModel.onIntent(HomeIntent.HeroClicked) },
        onAnnouncementClick = { url -> viewModel.onIntent(HomeIntent.AnnouncementClicked(url)) },
        onSeeAllAnnouncementsClick = { viewModel.onIntent(HomeIntent.AllAnnouncementsClicked) },
        onSocialClick = { url -> viewModel.onIntent(HomeIntent.SocialClicked(url)) },
        modifier = modifier,
    )
}
