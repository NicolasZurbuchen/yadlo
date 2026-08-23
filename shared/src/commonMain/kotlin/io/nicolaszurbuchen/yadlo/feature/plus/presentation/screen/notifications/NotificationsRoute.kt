package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.nicolaszurbuchen.yadlo.infra.notification.rememberNotificationPermissionRequester
import io.nicolaszurbuchen.yadlo.infra.notification.rememberSystemNotificationSettingsLauncher
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NotificationsRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val permissionRequester = rememberNotificationPermissionRequester()
    val systemSettingsLauncher = rememberSystemNotificationSettingsLauncher()

    LaunchedEffect(Unit) {
        viewModel.labels.collect { label ->
            when (label) {
                NotificationsLabel.RequestPermission -> {
                    permissionRequester.request { granted ->
                        viewModel.onIntent(NotificationsIntent.PermissionAnswered(granted))
                    }
                }

                NotificationsLabel.OpenSystemSettings -> {
                    systemSettingsLauncher.open()
                }
            }
        }
    }

    // **The half of this screen that happens somewhere else.** Sending somebody to system settings
    // means the answer changes while this screen is stopped, and coming back to a switch still
    // insisting it is blocked would make the button look broken. Every resume re-reads it, which
    // also covers a visitor who turned notifications off from outside the app entirely.
    LifecycleResumeEffect(Unit) {
        viewModel.onIntent(NotificationsIntent.PermissionRechecked)
        onPauseOrDispose { }
    }

    NotificationsScreen(
        state = state,
        onBackClick = onNavigateBack,
        onEnabledChange = { enabled -> viewModel.onIntent(NotificationsIntent.EnabledChanged(enabled)) },
        onSystemSettingsClick = { viewModel.onIntent(NotificationsIntent.SystemSettingsClicked) },
        modifier = modifier,
    )
}
