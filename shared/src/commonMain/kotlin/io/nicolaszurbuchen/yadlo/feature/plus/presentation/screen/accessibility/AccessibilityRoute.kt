package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.accessibility

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AccessibilityRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AccessibilityViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.labels.collect { label ->
            when (label) {
                is AccessibilityLabel.OpenUrl -> uriHandler.openUri(label.url)
            }
        }
    }

    AccessibilityScreen(
        state = state,
        onBackClick = onNavigateBack,
        onContactClick = { email -> viewModel.onIntent(AccessibilityIntent.ContactClicked(email)) },
        modifier = modifier,
    )
}
