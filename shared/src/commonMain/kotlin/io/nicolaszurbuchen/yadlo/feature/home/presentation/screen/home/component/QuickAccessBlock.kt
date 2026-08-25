package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.component.YadloEntryCard
import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloEntryUiModel
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.HomeBlockUiModel
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.QuickAccessItemUiModel
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.uimodel.QuickAccessEntryUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * The Phase's shortlist, drawn as a titled card of full-width rows.
 *
 * **The same object the Plus tab draws, not a lookalike.** These rows open Plus screens, so they are
 * the Plus tab's rows — same height, same icon size, same trailing mark, because a reader who taps
 * *Paiement* here and *Paiement* there has tapped one thing and should not be able to tell the two
 * apart. Sharing the component rather than matching it by eye is what keeps that true after the next
 * change to either screen.
 *
 * What makes it Accueil's rather than Plus's is the heading, which belongs to the Phase — *Préparer
 * sa venue* over payment and transport says why those two and why now, where a neutral "accès
 * rapide" would say nothing — and the length, which is one to three rather than sixteen.
 *
 * No subtitles. Plus writes the little it knows under a row because that tab is a table of contents
 * someone is scanning; here the heading has already said why the row is on the screen, and a second
 * line under each would turn three promoted rows back into a list to read through.
 */
@Composable
fun QuickAccessBlock(
    block: HomeBlockUiModel.QuickAccess,
    onItemClick: (QuickAccessItemUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    YadloEntryCard(
        title = block.title.asString(),
        items = block.items,
        entryOf = { item ->
            YadloEntryUiModel(
                icon = item.entry.icon,
                title = UiText.Resource(item.entry.title),
                subtitle = null,
                mark = item.entry.mark,
            )
        },
        onEntryClick = onItemClick,
        modifier = modifier,
    )
}
