package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
 * **The bar's own blue, continuing it.** These are chrome — they do not scroll away with the list,
 * which is half the point of them: a filter you have to scroll back up to change is a filter that
 * gets used once — so they are the bottom of the toolbar rather than the top of the page. No rule
 * closes the block off any more; where the blue stops is where the chrome stops.
 *
 * **The chips are rebuilt for that ground rather than inherited onto it.** The page-ground roles do
 * not survive on #74AEE0 — the outline measures 1.6:1 and the label 2.4:1 — so every chip here takes
 * the ink the blue carries, for its label and for a drawn edge it keeps even when filled. That edge
 * is the part that matters: a Category fill measures between 1.2:1 and 2.1:1 against this blue, so a
 * selected chip in Material's borderless style would have no visible boundary at all.
 *
 * The Category dot keeps its hue untouched at whatever it measures. It is a swatch beside a word
 * that already says the same thing, and what separates *enfants* gold from *musique* magenta at a
 * glance is the hue, not the luminance a contrast ratio measures.
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
    Column(modifier = modifier.fillMaxWidth().background(MaterialTheme.appColors.primarySubtle)) {
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
    }
}

// Half a spacing step, which is what closing the two rows' own vertical padding back up to one gap
// costs. Any more and the chips touch.
private val ROW_OVERLAP = 6.dp
