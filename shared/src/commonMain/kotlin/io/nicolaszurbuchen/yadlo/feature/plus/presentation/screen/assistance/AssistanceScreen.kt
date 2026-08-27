package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.design.component.YadloFactRow
import io.nicolaszurbuchen.yadlo.design.component.YadloLinkTile
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.uimodel.YadloFactMarkUiModel
import io.nicolaszurbuchen.yadlo.design.uimodel.YadloLinkMarkUiModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusBodyText
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusDetailScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusSection
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance.component.EmergencyNumberRow
import io.nicolaszurbuchen.yadlo.infra.ui.asString
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.assistance_lost_body
import yadlo.shared.generated.resources.assistance_section_emergency
import yadlo.shared.generated.resources.assistance_section_lost
import yadlo.shared.generated.resources.assistance_section_recognition
import yadlo.shared.generated.resources.assistance_title

/**
 * *En cas de besoin* — the numbers first, then where a lost bag goes.
 *
 * **It says only what the content stands behind.** The prototype put a first aid post and a
 * children's meeting point under these numbers as well; neither is published, and inventing them
 * here is the one place in the app where being wrong has a cost worth naming. *Reconnaître
 * l'équipe* is here because the content now carries it, not because the shape wanted filling.
 *
 * A rule between numbers rather than spacing alone: four large figures in one stack read as one
 * block, and the point of the screen is that each is a different call.
 *
 * The numbers get a column of their own so that nothing sits between a rule and the row it closes.
 * The section's own spacing was pushing the rules 8dp clear of the rows on either side, which left
 * a tap lighting up a strip floating between two lines. The row pads itself instead, inside its
 * touch target, so the ripple runs the row's full height and stops exactly where the rule is.
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
                Column(modifier = Modifier.fillMaxWidth()) {
                    state.numbers.forEachIndexed { index, number ->
                        if (index > 0) {
                            HorizontalDivider(color = MaterialTheme.appColors.borderSubtle)
                        }

                        EmergencyNumberRow(number = number, onClick = onNumberClick)
                    }
                }
            }
        }

        if (state.recognition.isNotEmpty()) {
            PlusSection(title = stringResource(Res.string.assistance_section_recognition)) {
                state.recognition.forEach {
                    YadloFactRow(mark = YadloFactMarkUiModel.CHECK, fact = it)
                }
            }
        }

        state.lostPropertyEmail?.let { email ->
            PlusSection(title = stringResource(Res.string.assistance_section_lost)) {
                PlusBodyText(text = stringResource(Res.string.assistance_lost_body))

                YadloLinkTile(
                    label = email,
                    mark = YadloLinkMarkUiModel.MAIL,
                    onClick = { onLostPropertyClick(email) },
                )
            }
        }
    }
}
