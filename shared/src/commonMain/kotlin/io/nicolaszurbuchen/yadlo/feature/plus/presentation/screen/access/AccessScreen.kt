package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.design.component.YadloFactRow
import io.nicolaszurbuchen.yadlo.design.component.YadloLinkTile
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.uimodel.YadloLinkMarkUiModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusBodyText
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusDetailScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusSection
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access.component.NightDeparturesBlock
import io.nicolaszurbuchen.yadlo.infra.text.asString
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.access_title

/**
 * *Accès* — one section per published mode, from the bus to arriving by water.
 *
 * The modes are whatever the content declares, in its order. Nothing here is a Kotlin list of
 * transport types: a shuttle laid on for one edition, or a mode that stops running, is a content
 * edit and no app release. That order is chronological — coming, then going home — which is how the
 * page is read before leaving the house, and the accepted cost is that at 02:00 the last bus takes
 * a little scrolling to find.
 *
 * **Marked facts where the mode is a list of conditions, prose only where it is genuinely a
 * sentence.** *Lignes 701 et 705, arrêt Préverenges, Village · cinq minutes à pied · plancher
 * surbaissé* was one paragraph, which made someone checking whether the bus works for them read all
 * of it to find their line. The ⓘ against the ✓ is what separates the two places reserved by the
 * entrance from the warning that the rest are not.
 */
@Composable
fun AccessScreen(
    state: AccessUiModel,
    onBackClick: () -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    PlusDetailScaffold(
        title = stringResource(Res.string.access_title),
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

        state.modes.forEach { mode ->
            PlusSection(title = mode.name) {
                mode.body?.let { PlusBodyText(text = it) }

                mode.facts.forEach { fact ->
                    YadloFactRow(mark = fact.mark, fact = fact.text)
                }

                mode.nights.forEach { NightDeparturesBlock(night = it) }

                mode.links.forEach { link ->
                    YadloLinkTile(
                        label = link.label,
                        mark = YadloLinkMarkUiModel.EXTERNAL,
                        onClick = { onLinkClick(link.url) },
                        sublabel = link.sublabel,
                    )
                }
            }
        }
    }
}
