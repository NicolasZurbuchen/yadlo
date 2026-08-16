package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.privacy

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** No store: the app collects nothing, so there is nothing here for content to change. */
@Composable
fun PrivacyRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PrivacyScreen(
        onBackClick = onNavigateBack,
        modifier = modifier,
    )
}
