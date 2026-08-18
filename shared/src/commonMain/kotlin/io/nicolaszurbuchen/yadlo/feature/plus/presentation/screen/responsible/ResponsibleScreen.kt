package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.responsible

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.component.YadloLinkTile
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloLinkMarkUiModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusBodyText
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusDetailScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusSection
import io.nicolaszurbuchen.yadlo.infra.ui.asString
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.plus_entry_responsible

/**
 * *Festival responsable* — one section per charter the association has signed.
 *
 * A charter is a heading, a paragraph and a link to whoever runs it, so the screen is a heading, a
 * paragraph and a link. It was written as a parameterised gabarit while a second entry shared the
 * shape; that entry became the foot of the tab, and a template with one caller is a template that
 * has stopped earning the indirection. Generalising it again is a refactor for the day there are
 * two pages to generalise, and this is the version that reads plainly until then.
 *
 * The default skeleton is exactly right here: prose under headings is what this is.
 */
@Composable
fun ResponsibleScreen(
    state: ResponsibleUiModel,
    onBackClick: () -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    PlusDetailScaffold(
        title = stringResource(Res.string.plus_entry_responsible),
        onBackClick = onBackClick,
        isLoading = state.isLoading,
        modifier = modifier,
    ) {
        state.emptyMessage?.let {
            Text(
                text = it.asString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.appColors.textSecondary,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        state.sections.forEach { section ->
            PlusSection(title = section.title) {
                section.body?.let { PlusBodyText(text = it) }

                section.links.forEach { link ->
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
