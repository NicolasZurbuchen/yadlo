package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.cleardata

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusBodyText
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusDetailScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusSection
import io.nicolaszurbuchen.yadlo.infra.ui.asString
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.clear_data_body
import yadlo.shared.generated.resources.clear_data_confirm_body
import yadlo.shared.generated.resources.clear_data_confirm_cancel
import yadlo.shared.generated.resources.clear_data_confirm_confirm
import yadlo.shared.generated.resources.clear_data_confirm_title
import yadlo.shared.generated.resources.plus_entry_clear_data

/**
 * *Effacer mes données* — the two things the app keeps, each with what it amounts to and a button.
 *
 * **The reading order is what it costs, then what there is, then the button.** A destructive control
 * put first is one a thumb reaches before the sentence explaining it, and this is the one screen in
 * the app where that matters: half of what it removes cannot be got back.
 *
 * **Only one of the two asks.** The Plan is something the visitor built over a weekend and there is
 * nothing to restore it from; the photographs are a copy of something the network still has. Giving
 * both the same dialog would train the answer to it, which is the ordinary way a confirmation stops
 * being one.
 *
 * **A button with nothing to remove is disabled rather than hidden.** The row still has something to
 * say — *rien d'enregistré* — and a control that disappears when it would be a no-op leaves a reader
 * wondering whether the screen has finished loading, which is precisely the doubt this screen must
 * not create.
 */
@Composable
fun ClearDataScreen(
    state: ClearDataUiModel,
    onBackClick: () -> Unit,
    onSavedClick: () -> Unit,
    onSavedConfirm: () -> Unit,
    onSavedDismiss: () -> Unit,
    onImagesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlusDetailScaffold(
        title = stringResource(Res.string.plus_entry_clear_data),
        onBackClick = onBackClick,
        isLoading = state.isLoading,
        modifier = modifier,
    ) {
        PlusBodyText(text = stringResource(Res.string.clear_data_body))

        ClearDataBlock(row = state.saved, onClick = onSavedClick)

        ClearDataBlock(row = state.images, onClick = onImagesClick)
    }

    if (state.isConfirmingSaved) {
        AlertDialog(
            onDismissRequest = onSavedDismiss,
            title = { Text(text = stringResource(Res.string.clear_data_confirm_title)) },
            text = { Text(text = stringResource(Res.string.clear_data_confirm_body)) },
            confirmButton = {
                TextButton(onClick = onSavedConfirm) {
                    // The one place the polarity role is spent as a button label. It is the default
                    // action of the dialog and the irreversible one, so it is the word that has to
                    // look different from the other — colour rather than position, since a dialog's
                    // two buttons sit a thumb's width apart.
                    Text(
                        text = stringResource(Res.string.clear_data_confirm_confirm),
                        color = MaterialTheme.appColors.negative,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onSavedDismiss) {
                    Text(text = stringResource(Res.string.clear_data_confirm_cancel))
                }
            },
            containerColor = MaterialTheme.appColors.surface,
            titleContentColor = MaterialTheme.appColors.textPrimary,
            textContentColor = MaterialTheme.appColors.textSecondary,
        )
    }
}

/**
 * One of the two, drawn as a section header, its sentence, and the control on the app's own card —
 * the same card *Plus › Notifications* puts its switch on, because both are a screen's own state
 * rather than something the festival published.
 */
@Composable
private fun ClearDataBlock(
    row: ClearDataRowUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlusSection(title = row.title.asString(), modifier = modifier) {
        Text(
            text = row.body.asString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.appColors.textSecondary,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.appColors.surface)
                    .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
        ) {
            Text(
                text = row.detail.asString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.appColors.textPrimary,
                modifier = Modifier.weight(1f),
            )

            Button(onClick = onClick, enabled = row.isEnabled) {
                Text(text = row.action.asString())
            }
        }
    }
}
