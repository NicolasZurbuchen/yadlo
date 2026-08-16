package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.component.YadloFactRow
import io.nicolaszurbuchen.yadlo.app.design.component.YadloLinkTile
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.uimodel.FactMarkUiModel
import io.nicolaszurbuchen.yadlo.app.design.uimodel.LinkMarkUiModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusDetailScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusSection
import io.nicolaszurbuchen.yadlo.infra.ui.asString
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.payment_section_accepted
import yadlo.shared.generated.resources.payment_section_links
import yadlo.shared.generated.resources.payment_section_notes
import yadlo.shared.generated.resources.payment_section_refused
import yadlo.shared.generated.resources.payment_title

/**
 * *Paiement.* Carte et TWINT — and, the part that has to be read before leaving the house, nothing
 * else.
 *
 * The refusal gets a section of its own rather than a footnote under the accepted list. Someone
 * scanning for "do I need cash" reads section headers, and burying the answer in the last row of a
 * list of what works is how the association's own site lost it.
 *
 * The refusal is marked as well as sectioned — see [YadloFactRow] — so the odd one out is findable
 * without reading every line, while never being carried by colour alone.
 */
@Composable
fun PaymentScreen(
    state: PaymentUiModel,
    onBackClick: () -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    PlusDetailScaffold(
        title = stringResource(Res.string.payment_title),
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

        if (state.accepted.isNotEmpty()) {
            PlusSection(title = stringResource(Res.string.payment_section_accepted)) {
                state.accepted.forEach { YadloFactRow(mark = FactMarkUiModel.CHECK, fact = it) }
            }
        }

        if (state.refused.isNotEmpty()) {
            PlusSection(title = stringResource(Res.string.payment_section_refused)) {
                state.refused.forEach { YadloFactRow(mark = FactMarkUiModel.CROSS, fact = it) }
            }
        }

        if (state.notes.isNotEmpty()) {
            PlusSection(title = stringResource(Res.string.payment_section_notes)) {
                state.notes.forEach {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.appColors.textSecondary,
                    )
                }
            }
        }

        if (state.links.isNotEmpty()) {
            PlusSection(title = stringResource(Res.string.payment_section_links)) {
                state.links.forEach { link ->
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
