package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.ProgrammeViewUiModel
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.programme_view_catalogue
import yadlo.shared.generated.resources.programme_view_programme

/**
 * *Programme* or *Découvrir* — which of the tab's two questions the list below is answering.
 *
 * **A segmented control rather than two more filter chips, which is what the rows beneath it are.**
 * A chip row is a set of independent toggles over one list; this is one control with two exclusive
 * halves, and it changes what the list *is* rather than which part of it shows. Wearing the chips'
 * clothes would have made the third row of the chrome read as a third filter — and a reader who
 * treats it as one will conclude the app has a filter that empties the day.
 *
 * **Labelled, never an icon pair.** A list/grid glyph is the usual way to draw this and it would be
 * wrong here: the Catalogue exists because a visitor who does not know what the festival offers
 * cannot ask for it, and a control that says nothing is a control that reader will not press. Two
 * words cost one row of chrome and are the whole discovery affordance.
 *
 * On the chrome blue, with the same treatment [YadloFilterChip] takes there: the selected half is a
 * solid pill of the app's primary and the unselected half is the blue itself, edged in the ink that
 * blue carries. The default check icon is dropped — with two labelled halves the fill already says
 * which is on, and the icon takes the width off the words that have to be read.
 */
@Composable
fun ProgrammeViewToggle(
    selectedView: ProgrammeViewUiModel,
    onViewClick: (ProgrammeViewUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        ProgrammeViewUiModel.entries.forEachIndexed { index, view ->
            SegmentedButton(
                selected = view == selectedView,
                onClick = { onViewClick(view) },
                shape =
                    SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = ProgrammeViewUiModel.entries.size,
                    ),
                colors =
                    SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.appColors.primary,
                        activeContentColor = MaterialTheme.appColors.onPrimary,
                        activeBorderColor = MaterialTheme.appColors.primary,
                        inactiveContainerColor = Color.Transparent,
                        inactiveContentColor = MaterialTheme.appColors.onPrimarySubtle,
                        inactiveBorderColor = MaterialTheme.appColors.onPrimarySubtle,
                    ),
                icon = {},
                label = {
                    Text(
                        text =
                            when (view) {
                                ProgrammeViewUiModel.PROGRAMME -> stringResource(Res.string.programme_view_programme)
                                ProgrammeViewUiModel.CATALOGUE -> stringResource(Res.string.programme_view_catalogue)
                            },
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
            )
        }
    }
}
