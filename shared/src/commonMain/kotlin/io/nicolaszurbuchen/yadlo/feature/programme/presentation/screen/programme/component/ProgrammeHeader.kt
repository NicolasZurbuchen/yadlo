package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.common.content.presentation.component.SlotScaleRow
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotScaleUiModel
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.CategoryChipUiModel
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.DayChipUiModel
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.ProgrammeViewUiModel

/**
 * Which list, then the day, the kind, and the span the bars are drawn against — everything you set
 * before you read what is below.
 *
 * **Every row is the same chip, and the view row is simply the first of them.** It was briefly a
 * segmented button, spaced away from the rest so it would not read as a filter — see [ViewChipRow]
 * for why both halves of that were wrong. What separates a control that changes the screen from one
 * that filters it is its position and its labels, not eight pixels of height and a different corner
 * radius. In the Catalogue the day row and the scale are simply absent, so the chrome shrinks to the
 * two rows that still mean something.
 *
 * **Each row is pulled up by one overlap for every row above it.** Every chip row pads itself so it
 * can be used alone, and Material pads each chip again to a 48dp touch target, so two stacked rows
 * open a gap wider than either chip is tall. One [ROW_OVERLAP] per row above closes each of those
 * gaps to the same width, whether the block is two rows or three.
 *
 * **The bar's own blue, continuing it.** These are chrome — they do not scroll away with the list,
 * which is half the point of them: a filter you have to scroll back up to change is a filter that
 * gets used once — so they are the bottom of the toolbar rather than the top of the page. No rule
 * closes the block off any more; where the blue stops is where the chrome stops.
 *
 * **The chips are the chrome, wearing its ink.** The page's own roles do not survive on #74AEE0 —
 * the outline measures 1.6:1 and the label 2.4:1 — so both rows draw their edge and their label in
 * the ink the blue carries. The Category dot does not follow: it is a fill chosen against the page
 * and measures 1.2:1 to 2.1:1 here. It is a swatch beside a word that already says the same thing,
 * so the cost is a quieter dot rather than a lost one.
 *
 * A selected chip is a solid pill of the thing it stands for, with no edge of its own — the same as
 * on the stands list. Its boundary against the blue is the fill, which the eye finds by hue rather
 * than by luminance; that is the trade for a selected state that reads as one thing instead of two.
 */
@Composable
fun ProgrammeHeader(
    selectedView: ProgrammeViewUiModel?,
    days: List<DayChipUiModel>,
    categories: List<CategoryChipUiModel>,
    scale: SlotScaleUiModel?,
    onViewClick: (ProgrammeViewUiModel) -> Unit,
    onDayClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onAllCategoriesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Counted rather than derived from the flags one by one, because the Catalogue drops the day
    // row from the middle of the stack: the Category chips are the second row there and the third
    // on the Programme, and they have to close whichever gap is actually above them.
    var rowsAbove = 0

    Column(modifier = modifier.fillMaxWidth().background(MaterialTheme.appColors.primarySubtle)) {
        selectedView?.let { view ->
            ViewChipRow(selectedView = view, onViewClick = onViewClick)
            rowsAbove++
        }

        if (days.isNotEmpty()) {
            DayChipRow(
                days = days,
                onDayClick = onDayClick,
                modifier = Modifier.offset(y = -ROW_OVERLAP * rowsAbove),
            )
            rowsAbove++
        }

        if (categories.isNotEmpty()) {
            CategoryChipRow(
                categories = categories,
                onCategoryClick = onCategoryClick,
                onAllClick = onAllCategoriesClick,
                modifier = Modifier.offset(y = -ROW_OVERLAP * rowsAbove),
            )
        }

        scale?.let {
            // The readings sit over the positions they describe: the rows' own left edge, and the
            // right edge of the bar, which stops where the chevron column starts.
            // The same shift as the Category row above rather than one more, so the readings
            // stay glued to the row they belong to instead of climbing into it.
            SlotScaleRow(
                scale = it,
                modifier =
                    Modifier
                        .offset(y = -ROW_OVERLAP * rowsAbove)
                        .padding(
                            PaddingValues(
                                start = MaterialTheme.spacing.md,
                                end = MaterialTheme.spacing.sm + CHEVRON_SIZE + MaterialTheme.spacing.sm,
                                bottom = MaterialTheme.spacing.xs,
                            ),
                        ),
            )
        }
    }
}

// Half a spacing step, which is what closing one row's own vertical padding back up to a single gap
// costs. Any more and the chips touch.
private val ROW_OVERLAP = 6.dp
