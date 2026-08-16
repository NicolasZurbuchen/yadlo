package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.component.YadloLinkTile
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.uimodel.LinkMarkUiModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusBodyText
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusDetailScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusSection
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access.component.NightDeparturesBlock
import io.nicolaszurbuchen.yadlo.infra.ui.asString
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.access_title

/**
 * *Accès* — one section per published mode, from on foot to across the water.
 *
 * The modes are whatever the content declares, in its order. Nothing here is a Kotlin list of
 * transport types: a shuttle laid on for one edition, or a mode that stops running, is a content
 * edit and no app release.
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

                mode.nights.forEach { NightDeparturesBlock(night = it) }

                mode.links.forEach { link ->
                    YadloLinkTile(
                        label = link.label,
                        mark = LinkMarkUiModel.EXTERNAL,
                        onClick = { onLinkClick(link.url) },
                        sublabel = link.sublabel,
                    )
                }
            }
        }
    }
}
