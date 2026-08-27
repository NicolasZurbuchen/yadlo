package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import io.nicolaszurbuchen.yadlo.infra.format.mailtoUrl
import io.nicolaszurbuchen.yadlo.infra.platform.BuildFlags
import org.koin.compose.koinInject

/**
 * No ViewModel and no store: nothing on this screen comes from the content, and the one fact that
 * is not an app string — the version — is a property of the binary that cannot change while the
 * screen is open. A store observing a constant would be apparatus around nothing.
 *
 * That is the whole difference between *À propos* and the rest of the tab. Everything else says what
 * the festival published; this one says what the app is and who to write to about it.
 */
@Composable
fun AboutRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val buildFlags = koinInject<BuildFlags>()
    val uriHandler = LocalUriHandler.current

    AboutScreen(
        state = AboutUiModel(version = buildFlags.version),
        onBackClick = onNavigateBack,
        // Opened here rather than published as a Label, for want of a store to publish it from. It
        // leaves the app either way, which is the part that matters.
        onEmailClick = { address -> uriHandler.openUri(mailtoUrl(address)) },
        modifier = modifier,
    )
}
