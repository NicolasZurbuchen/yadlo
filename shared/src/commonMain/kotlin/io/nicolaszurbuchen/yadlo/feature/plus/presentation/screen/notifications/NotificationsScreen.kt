package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusBodyText
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusDetailScaffold
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.notifications_blocked_action
import yadlo.shared.generated.resources.notifications_blocked_body
import yadlo.shared.generated.resources.notifications_body
import yadlo.shared.generated.resources.notifications_switch_label
import yadlo.shared.generated.resources.plus_entry_notifications

/**
 * *Notifications* — the one switch, and the sentence explaining why it will not move.
 *
 * The paragraph above the switch says what the app would send rather than describing the switch,
 * because a control labelled *Rappels* under a heading reading *Notifications* has already said
 * everything a label could. What a visitor cannot know without being told is what arrives and when.
 */
@Composable
fun NotificationsScreen(
    state: NotificationsUiModel,
    onBackClick: () -> Unit,
    onEnabledChange: (Boolean) -> Unit,
    onSystemSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlusDetailScaffold(
        title = stringResource(Res.string.plus_entry_notifications),
        onBackClick = onBackClick,
        isLoading = state.isLoading,
        modifier = modifier,
    ) {
        PlusBodyText(text = stringResource(Res.string.notifications_body))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.appColors.surface)
                    .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
        ) {
            Text(
                text = stringResource(Res.string.notifications_switch_label),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.appColors.textPrimary,
            )

            Switch(checked = state.isEnabled, onCheckedChange = onEnabledChange)
        }

        // Only when the two answers disagree in the one direction the visitor cannot fix from here.
        if (state.isBlockedBySystem) {
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(Res.string.notifications_blocked_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.appColors.textSecondary,
                )

                Button(onClick = onSystemSettingsClick) {
                    Text(text = stringResource(Res.string.notifications_blocked_action))
                }
            }
        }
    }
}
