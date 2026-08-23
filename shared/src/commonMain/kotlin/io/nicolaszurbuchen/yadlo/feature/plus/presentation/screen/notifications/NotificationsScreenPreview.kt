package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.app.design.theme.YadloTheme
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors

/**
 * The three states the screen actually has, built through the real mapper rather than by hand — the
 * blocked one in particular is a *combination* of the two switches, and a preview that asserted it
 * directly would stop tracking the rule that produces it.
 */
private class NotificationsStateProvider : PreviewParameterProvider<NotificationsUiModel> {
    override val values =
        sequenceOf(
            NotificationsState(isEnabled = true, isPermissionGranted = true).toUiModel(),
            NotificationsState(isEnabled = false, isPermissionGranted = true).toUiModel(),
            NotificationsState(isEnabled = true, isPermissionGranted = false).toUiModel(),
        )
}

@Preview
@Composable
private fun NotificationsScreenPreview(
    @PreviewParameter(NotificationsStateProvider::class) state: NotificationsUiModel,
) {
    YadloTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            NotificationsScreen(state = state, onBackClick = {}, onEnabledChange = {}, onSystemSettingsClick = {})
        }
    }
}

@Preview
@Composable
private fun NotificationsScreenDarkPreview(
    @PreviewParameter(NotificationsStateProvider::class) state: NotificationsUiModel,
) {
    YadloTheme(darkTheme = true) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            NotificationsScreen(state = state, onBackClick = {}, onEnabledChange = {}, onSystemSettingsClick = {})
        }
    }
}
