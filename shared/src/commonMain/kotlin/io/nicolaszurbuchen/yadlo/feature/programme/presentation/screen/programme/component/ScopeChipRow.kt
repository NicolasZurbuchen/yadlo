package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.design.component.YadloFilterChip
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.ScopeChipUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * *Découvrir · Tous · Vendredi · Samedi · Dimanche* — the one row that says what the tab is showing.
 *
 * **It was two rows.** A segmented view toggle sat above a row of day chips, which put three chip
 * rows and an axis between the top of the screen and the first row of a list that is fifteen rows
 * long. Merging them costs nothing because the split was never real: every chip here answers *what
 * am I looking at*, and it happens that three of the answers are days. Making that one exclusive
 * selection in the state — `ProgrammeScopeState` — is what let the two rows become one.
 *
 * **Découvrir is first because it is the odd one**, and putting it at the head of the row is what
 * stops it reading as a fourth day. *Tous* follows it, then the days in order, so the row runs from
 * the widest thing you can be looking at to the narrowest.
 *
 * A day chip is also the way out of the Catalogue, which is a job it can do precisely because it
 * cannot filter one: nothing in the Catalogue has a day, so tapping Samedi there is unambiguous —
 * it means show me the Saturday, and a Saturday is a timetable.
 *
 * A [LazyRow] rather than a scrolling [androidx.compose.foundation.layout.Row]: five chips is this
 * edition's number and a fourth festival day would make it six.
 *
 * The chip is the chrome and draws its edge in the ink the blue carries — see [ProgrammeHeader] for
 * why a control on this ground cannot inherit the page's roles.
 */
@Composable
fun ScopeChipRow(
    scopes: List<ScopeChipUiModel>,
    onScopeClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        // Inside the scroll, so the chips carry it past both screen edges rather than stopping
        // short of them.
        contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.md),
        modifier = modifier.fillMaxWidth(),
    ) {
        items(items = scopes, key = { it.id }) { chip ->
            YadloFilterChip(
                label = chip.label.asString(),
                isSelected = chip.isSelected,
                onClick = { onScopeClick(chip.id) },
                container = MaterialTheme.appColors.primarySubtle,
                outline = MaterialTheme.appColors.onPrimarySubtle,
            )
        }
    }
}
