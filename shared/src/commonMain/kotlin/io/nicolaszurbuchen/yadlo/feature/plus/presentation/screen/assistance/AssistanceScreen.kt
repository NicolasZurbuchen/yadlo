package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusBodyText
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusDetailScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusLinkTile
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusSection
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance.component.EmergencyNumberRow
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.uimodel.PlusMarkUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.asString
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.assistance_lost_body
import yadlo.shared.generated.resources.assistance_section_emergency
import yadlo.shared.generated.resources.assistance_section_lost
import yadlo.shared.generated.resources.assistance_title

/**
 * *En cas de besoin* — the numbers first, then where a lost bag goes.
 *
 * **The screen is currently its most reliable half, and says nothing more.** The prototype put a
 * first aid post, a children's meeting point and how to recognise a volunteer under these numbers;
 * none of it is published, and inventing any of it here is the one place in the app where being
 * wrong has a cost worth naming. What is left is true without anyone's confirmation.
 */
@Composable
fun AssistanceScreen(
    state: AssistanceUiModel,
    onBackClick: () -> Unit,
    onNumberClick: (String) -> Unit,
    onLostPropertyClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    PlusDetailScaffold(
        title = stringResource(Res.string.assistance_title),
        onBackClick = onBackClick,
        isLoading = state.isLoading,
        modifier = modifier,
    ) {
        state.emptyMessage?.let { message ->
            Text(
                text = message.asString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.appColors.textSecondary,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (state.numbers.isNotEmpty()) {
            PlusSection(title = stringResource(Res.string.assistance_section_emergency)) {
                state.numbers.forEach { number ->
                    EmergencyNumberRow(number = number, onClick = onNumberClick)
                }
            }
        }

        state.lostPropertyEmail?.let { email ->
            PlusSection(title = stringResource(Res.string.assistance_section_lost)) {
                PlusBodyText(text = stringResource(Res.string.assistance_lost_body))

                PlusLinkTile(
                    label = email,
                    mark = PlusMarkUiModel.MAIL,
                    onClick = { onLostPropertyClick(email) },
                )
            }
        }
    }
}
