package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusBodyText
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusDetailScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusLinkTile
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusSection
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * The gabarit: a title, some paragraphs, some links.
 *
 * *Festival responsable* and *Réseaux sociaux* are this screen with different words in it, and so
 * is whatever the association publishes next. What it is deliberately not is a layout language —
 * an entry that needs more than a heading, a paragraph and a link has earned a screen of its own,
 * which is how Horaires and Paiement got theirs.
 */
@Composable
fun PageScreen(
    state: PageUiModel,
    onBackClick: () -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    PlusDetailScaffold(
        title = state.title.asString(),
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
            if (section.title != null) {
                PlusSection(title = section.title) {
                    section.body?.let { PlusBodyText(text = it) }

                    section.links.forEach { link ->
                        PlusLinkTile(
                            label = link.label,
                            mark = EXTERNAL_MARK,
                            onClick = { onLinkClick(link.url) },
                            sublabel = link.sublabel,
                        )
                    }
                }
            } else {
                // An untitled section is the page's whole body — the title above it already said
                // what these are, and a second heading repeating it is noise.
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    section.body?.let { PlusBodyText(text = it) }

                    section.links.forEach { link ->
                        PlusLinkTile(
                            label = link.label,
                            mark = EXTERNAL_MARK,
                            onClick = { onLinkClick(link.url) },
                            sublabel = link.sublabel,
                        )
                    }
                }
            }
        }
    }
}

private const val EXTERNAL_MARK = "↗"
