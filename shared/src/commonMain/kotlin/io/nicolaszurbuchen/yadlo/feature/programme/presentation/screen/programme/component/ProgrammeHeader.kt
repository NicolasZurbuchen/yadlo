package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.CategoryChipUiModel
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.DayChipUiModel
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.ProgrammeScaleUiModel

/**
 * The day, the kind, and the span the bars are drawn against — everything you set before you read
 * the list.
 *
 * **Drawn as part of the toolbar rather than as the first thing in the list.** It sat on the page
 * ground directly under the shell's bar, so a screen with one control surface looked like it had
 * two: a title block, then a gap, then three unlabelled rows that were somehow not part of it. On
 * the bar's own surface with a single rule under the lot, the bar and the filters read as one piece
 * of chrome and the list starts where the rule ends.
 *
 * It does not scroll away with the list, which is the other half of the point: a filter you have to
 * scroll back up to change is a filter that gets used once.
 */
@Composable
fun ProgrammeHeader(
    days: List<DayChipUiModel>,
    categories: List<CategoryChipUiModel>,
    scale: ProgrammeScaleUiModel?,
    onDayClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onAllCategoriesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().background(MaterialTheme.appColors.surface)) {
        if (days.isNotEmpty()) {
            DayChipRow(days = days, onDayClick = onDayClick)
        }

        if (categories.isNotEmpty()) {
            CategoryChipRow(
                categories = categories,
                onCategoryClick = onCategoryClick,
                onAllClick = onAllCategoriesClick,
            )
        }

        scale?.let {
            ProgrammeScaleRow(scale = it, modifier = Modifier.padding(bottom = MaterialTheme.spacing.xs))
        }

        // The one line on this screen that is not between two Slots: it closes the chrome off from
        // the list rather than separating two rows of it.
        HorizontalDivider(color = MaterialTheme.appColors.borderSubtle)
    }
}
