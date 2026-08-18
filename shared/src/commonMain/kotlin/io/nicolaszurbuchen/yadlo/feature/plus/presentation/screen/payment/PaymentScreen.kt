package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.component.YadloFactRow
import io.nicolaszurbuchen.yadlo.app.design.component.YadloHero
import io.nicolaszurbuchen.yadlo.app.design.component.YadloLinkTile
import io.nicolaszurbuchen.yadlo.app.design.uimodel.FactMarkUiModel
import io.nicolaszurbuchen.yadlo.app.design.uimodel.LinkMarkUiModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusBodyText
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusDetailScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusSection
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment.component.PaymentSkeleton
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.payment_section_accepted
import yadlo.shared.generated.resources.payment_title

/**
 * *Paiement.* Carte et TWINT — and, the part that has to be read before leaving the house, nothing
 * else.
 *
 * **The answer opens the screen.** It used to open with a section header called *Accepté partout*,
 * which made a reader whose only question is "do I need cash" assemble the sentence out of a list
 * before they could put the phone away. The hero says it in three words; the list under it is for
 * the one holding a Maestro card who wants to be sure.
 *
 * **One list, not two.** Accepted and refused were two sections, so being sure about the refusal
 * meant reading both. In a single column, with the ✕ tinted against three ✓, the odd one out is
 * findable without reading a word — while never being carried by colour alone, since it also has a
 * glyph and a content description.
 *
 * Each note keeps its own heading and its own links, which is the only arrangement that puts
 * twint.ch under *Vous n'avez pas TWINT ?* rather than in a bin of links at the foot of the page.
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
        skeleton = { PaymentSkeleton() },
        modifier = modifier,
    ) {
        state.headline?.let { YadloHero(title = it, body = state.summary) }

        if (state.methods.isNotEmpty()) {
            PlusSection(title = stringResource(Res.string.payment_section_accepted)) {
                state.methods.forEach { method ->
                    YadloFactRow(
                        mark = if (method.accepted) FactMarkUiModel.CHECK else FactMarkUiModel.CROSS,
                        fact = method.name,
                    )
                }
            }
        }

        state.notes.forEach { note ->
            PlusSection(title = note.title) {
                PlusBodyText(text = note.body)

                note.links.forEach { link ->
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
