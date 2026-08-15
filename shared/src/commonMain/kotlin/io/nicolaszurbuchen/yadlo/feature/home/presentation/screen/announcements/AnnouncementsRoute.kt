package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.announcements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AnnouncementsRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AnnouncementsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onNavigateBackUpdated by rememberUpdatedState(onNavigateBack)
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.labels.collect { label ->
            when (label) {
                is AnnouncementsLabel.OpenUrl -> uriHandler.openUri(label.url)
            }
        }
    }

    AnnouncementsScreen(
        state = state,
        onBackClick = { onNavigateBackUpdated() },
        onAnnouncementClick = { url -> viewModel.onIntent(AnnouncementsIntent.AnnouncementClicked(url)) },
        modifier = modifier,
    )
}
