package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * No ViewModel and no store: every word on this screen is an app string, not content.
 *
 * That is the whole difference between *À propos* and the rest of the tab — everything else says
 * what the festival published, and this one says what the app is and who to blame for it.
 */
@Composable
fun AboutRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AboutScreen(
        onBackClick = onNavigateBack,
        modifier = modifier,
    )
}
