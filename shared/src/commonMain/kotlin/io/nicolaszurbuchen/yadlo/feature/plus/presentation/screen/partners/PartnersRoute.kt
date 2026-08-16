package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PartnersRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PartnersViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.labels.collect { label ->
            when (label) {
                is PartnersLabel.OpenUrl -> uriHandler.openUri(label.url)
            }
        }
    }

    PartnersScreen(
        state = state,
        onBackClick = onNavigateBack,
        onPartnerClick = { url -> viewModel.onIntent(PartnersIntent.PartnerClicked(url)) },
        modifier = modifier,
    )
}
