package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.CategoryChipUiModel
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.DayChipUiModel
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.ProgrammeScaleUiModel

/**
 * The day, the kind, and the span the bars are drawn against — everything you set before you read
 * the list.
 *
 * **One block on the page ground, closed off by a single rule.** It was drawn on a surface of its
 * own for a while, so the bar and the filters would read as one piece of chrome — but the bar is the
 * bandeau blue now, and a filter row on that blue cannot be built: an unselected chip's outline
 * measures 1.6:1 against it and a selected one's fill 2.8:1, so the control disappears into its own
 * background. The bar being a colour of its own is what separates chrome from page; these are
 * controls over a list, sitting on the same ground the list does, with a rule where they end.
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
    Column(modifier = modifier.fillMaxWidth().background(MaterialTheme.appColors.background)) {
        if (days.isNotEmpty()) {
            DayChipRow(days = days, onDayClick = onDayClick)
        }

        if (categories.isNotEmpty()) {
            // Negative offset rather than smaller row padding: each row pads itself so it can be
            // used alone, and stacked they add up to a gap wider than either chip is tall.
            CategoryChipRow(
                categories = categories,
                onCategoryClick = onCategoryClick,
                onAllClick = onAllCategoriesClick,
                modifier = Modifier.offset(y = -ROW_OVERLAP),
            )
        }

        scale?.let {
            ProgrammeScaleRow(
                scale = it,
                modifier = Modifier.offset(y = -ROW_OVERLAP).padding(bottom = MaterialTheme.spacing.xs),
            )
        }

        // The one line on this screen that is not between two Slots: it closes the chrome off from
        // the list rather than separating two rows of it.
        HorizontalDivider(color = MaterialTheme.appColors.borderSubtle)
    }
}

// Half a spacing step, which is what closing the two rows' own vertical padding back up to one gap
// costs. Any more and the chips touch.
private val ROW_OVERLAP = 6.dp
