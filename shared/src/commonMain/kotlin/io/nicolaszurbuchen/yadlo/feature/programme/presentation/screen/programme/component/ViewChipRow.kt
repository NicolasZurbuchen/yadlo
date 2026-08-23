package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.component.YadloFilterChip
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.ProgrammeViewUiModel
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.programme_view_catalogue
import yadlo.shared.generated.resources.programme_view_programme

/**
 * *Programme · Découvrir* — which of the tab's two questions the list below is answering.
 *
 * **[YadloFilterChip], and not a segmented button.** *Reversed: this was a Material
 * `SingleChoiceSegmentedButtonRow`*, on the reasoning that a chip row is a set of independent
 * toggles over one list while this is one control with two exclusive halves — so wearing the chips'
 * clothes would make it read as a third filter. Two things were wrong with that. The premise is
 * false: [DayChipRow] is exclusive too, and nobody reads *Vendredi · Samedi · Dimanche* as three
 * independent toggles. And the price was paid in the wrong currency — a segmented button is 40dp
 * tall with fully rounded ends against a 32dp chip with an 8dp corner, so the top of the chrome
 * carried a control eight pixels taller and a different shape from the two rows under it, which is
 * the sort of difference nobody can name and everybody sees.
 *
 * What keeps it from reading as a filter is where it sits and what it says, not what it is made of:
 * it is the first row of the block, and its labels name screens rather than subsets.
 *
 * **Labelled, never an icon pair.** A list/grid glyph is the usual way to draw this and it would be
 * wrong here: the Catalogue exists because a visitor who does not know what the festival offers
 * cannot ask for it, and a control that says nothing is a control that reader will not press.
 *
 * Scrollable for the same reason [DayChipRow] is — two is the number of views, not a guarantee that
 * two labels fit a row at every text size.
 */
@Composable
fun ViewChipRow(
    selectedView: ProgrammeViewUiModel,
    onViewClick: (ProgrammeViewUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        modifier =
            modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(start = MaterialTheme.spacing.md, end = MaterialTheme.spacing.md, bottom = MaterialTheme.spacing.xs),
    ) {
        ProgrammeViewUiModel.entries.forEach { view ->
            val label =
                when (view) {
                    ProgrammeViewUiModel.PROGRAMME -> Res.string.programme_view_programme
                    ProgrammeViewUiModel.CATALOGUE -> Res.string.programme_view_catalogue
                }

            YadloFilterChip(
                label = stringResource(label),
                isSelected = view == selectedView,
                onClick = { onViewClick(view) },
                container = MaterialTheme.appColors.primarySubtle,
                outline = MaterialTheme.appColors.onPrimarySubtle,
            )
        }
    }
}
