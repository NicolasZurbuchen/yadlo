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
import io.nicolaszurbuchen.yadlo.common.content.presentation.component.SlotScaleRow
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SlotScaleUiModel
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.CategoryChipUiModel
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.ScopeChipUiModel

/**
 * What the list is, then which part of it, then the span its bars are drawn against — everything
 * you set before you read what is below.
 *
 * **Two chip rows, and it was briefly three.** The view switch arrived as a segmented control of
 * its own above the days, which put three rows and an axis between the top of the screen and the
 * first of fifteen. It is one row with the days now, because it was always the same question — see
 * [ScopeChipRow]. What is left is the shortest this block can be while both questions stay
 * answerable without scrolling.
 *
 * **The scale is here only when the list is one day.** Under *Tous* each day's reading travels with
 * its own sticky header instead, because a span is a fact about one day and a single reading in the
 * toolbar could only be right about one of the three.
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
 *
 * **The second row is pulled up by one [ROW_OVERLAP].** Every chip row pads itself so it can be
 * used alone, and Material pads each chip again out to a 48dp touch target, so two stacked rows
 * open a gap wider than either chip is tall.
 */
@Composable
fun ProgrammeHeader(
    scopes: List<ScopeChipUiModel>,
    categories: List<CategoryChipUiModel>,
    scale: SlotScaleUiModel?,
    onScopeClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onAllCategoriesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().background(MaterialTheme.appColors.primarySubtle)) {
        if (scopes.isNotEmpty()) {
            ScopeChipRow(
                scopes = scopes,
                onScopeClick = onScopeClick,
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.xs),
            )
        }

        if (categories.isNotEmpty()) {
            CategoryChipRow(
                categories = categories,
                onCategoryClick = onCategoryClick,
                onAllClick = onAllCategoriesClick,
                modifier = Modifier.offset(y = -ROW_OVERLAP),
            )
        }

        scale?.let {
            // The readings sit over the positions they describe: the rows' own left edge, and the
            // right edge of the bar, which stops where the chevron column starts. The same shift as
            // the row above rather than one more, so they stay glued to it instead of climbing in.
            SlotScaleRow(
                scale = it,
                modifier =
                    Modifier
                        .offset(y = -ROW_OVERLAP)
                        .padding(
                            start = MaterialTheme.spacing.md,
                            end = MaterialTheme.spacing.sm + CHEVRON_SIZE + MaterialTheme.spacing.sm,
                            bottom = MaterialTheme.spacing.xs,
                        ),
            )
        }
    }
}

// Half a spacing step, which is what closing one row's own vertical padding back up to a single gap
// costs. Any more and the chips touch.
private val ROW_OVERLAP = 6.dp
