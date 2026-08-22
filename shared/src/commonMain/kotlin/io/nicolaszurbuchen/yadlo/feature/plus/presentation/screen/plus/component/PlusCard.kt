package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.component.YadloEntryCard
import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloEntryUiModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.PlusEntryUiModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.PlusGroupUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import org.jetbrains.compose.resources.stringResource

/**
 * One group of the root list, as a titled card of rows.
 *
 * The card and the row it draws live in the design system now — Accueil's phase block is the same
 * object with different words in it, and a component two features draw belongs in `app/`. What is
 * left here is the only part that is about the Plus tab: that a group's title comes off its id, and
 * that a row's icon, name and mark are fixed properties of *which entry it is* while the subtitle
 * comes from the content.
 */
@Composable
fun PlusCard(
    group: PlusGroupUiModel,
    onEntryClick: (PlusEntryUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    YadloEntryCard(
        title = stringResource(group.id.title),
        items = group.rows,
        entryOf = { row ->
            YadloEntryUiModel(
                icon = row.entry.icon,
                title = UiText.Resource(row.entry.title),
                subtitle = row.subtitle,
                mark = row.entry.mark,
            )
        },
        onEntryClick = { row -> onEntryClick(row.entry) },
        modifier = modifier,
    )
}
