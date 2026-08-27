package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.notifications

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.design.preview.YadloPreview
import io.nicolaszurbuchen.yadlo.infra.preview.PreviewThemes

/**
 * The three states the screen actually has, built through the real mapper rather than by hand — the
 * blocked one in particular is a *combination* of the two switches, and a preview that asserted it
 * directly would stop tracking the rule that produces it.
 */
private class NotificationsScreenStateProvider : PreviewParameterProvider<NotificationsUiModel> {
    override val values =
        sequenceOf(
            NotificationsState(isEnabled = true, isPermissionGranted = true).toUiModel(),
            NotificationsState(isEnabled = false, isPermissionGranted = true).toUiModel(),
            NotificationsState(isEnabled = true, isPermissionGranted = false).toUiModel(),
        )
}

@PreviewThemes
@Composable
private fun NotificationsScreenPreview(
    @PreviewParameter(NotificationsScreenStateProvider::class) state: NotificationsUiModel,
) {
    YadloPreview {
        NotificationsScreen(state = state, onBackClick = {}, onEnabledChange = {}, onSystemSettingsClick = {})
    }
}
