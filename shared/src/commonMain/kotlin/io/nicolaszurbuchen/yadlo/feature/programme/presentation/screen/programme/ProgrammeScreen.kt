package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.app.navigation.LocalTabChromeInsets
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component.CatalogueCard
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component.ProgrammeEmptyMessage
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component.ProgrammeHeader
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component.SlotRow

/**
 * The chrome, then whichever of the two lists is showing.
 *
 * Layout B2 — the day, the filters, then one chronological list — or the Catalogue: no day, no
 * axis, and a two-column staggered grid of everything the festival offers.
 *
 * **One screen with two bodies rather than two screens.** The two views answer questions a visitor
 * moves between in a single thought — "what is there to do" and "when is it on" — and they share
 * the Category filter, the tab, and the fiche every item opens. Splitting them would put a second
 * door onto the same Happenings in the navigation, which is the thing DECISIONS.md refuses twice
 * over; the toggle keeps it one door with two ways to look through it.
 *
 * The chip rows are chrome and stay put while the list scrolls under them — they are how you change
 * what the list is, so scrolling them away would mean scrolling back to change your mind.
 */
@Composable
fun ProgrammeScreen(
    state: ProgrammeUiModel,
    onViewClick: (ProgrammeViewUiModel) -> Unit,
    onDayClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onAllCategoriesClick: () -> Unit,
    onSlotClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
        return
    }

    // The shell's bars are drawn over this screen rather than beside it. The header clears the top
    // one here, and the list clears the bottom one in its own content padding.
    val chrome = LocalTabChromeInsets.current

    Column(modifier = modifier.fillMaxSize().padding(top = chrome.top)) {
        ProgrammeHeader(
            selectedView = state.view,
            days = state.days,
            categories = state.categories,
            scale = state.scale,
            onViewClick = onViewClick,
            onDayClick = onDayClick,
            onCategoryClick = onCategoryClick,
            onAllCategoriesClick = onAllCategoriesClick,
        )

        if (state.emptyMessage != null) {
            ProgrammeEmptyMessage(message = state.emptyMessage)
            return@Column
        }

        if (state.view == ProgrammeViewUiModel.CATALOGUE) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(CATALOGUE_COLUMNS),
                // Equal and tight both ways, the same as the stands grid: a gutter wider than the
                // gap between two cards separates the columns more than it separates the cards.
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                verticalItemSpacing = MaterialTheme.spacing.sm,
                contentPadding =
                    PaddingValues(
                        start = MaterialTheme.spacing.md,
                        top = MaterialTheme.spacing.md,
                        end = MaterialTheme.spacing.md,
                        bottom = chrome.bottom + MaterialTheme.spacing.md,
                    ),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(items = state.catalogue, key = { it.id }) { entry ->
                    CatalogueCard(entry = entry, onClick = onSlotClick)
                }
            }

            return@Column
        }

        LazyColumn(
            // Nothing of its own but the bar it has to clear. Each row already pads itself top and
            // bottom, and the list's own on top of that put a visible gap under the filter block
            // and left the first Slot looking detached from the chrome that filters it.
            contentPadding = PaddingValues(bottom = chrome.bottom),
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(state.rows, key = { _, row -> row.id }) { index, row ->
                // A hairline between neighbours, never around each of them: the shared left edge
                // and the common baseline are the whole reason this is a list of rows, not cards.
                if (index > 0) {
                    HorizontalDivider(color = MaterialTheme.appColors.borderSubtle)
                }

                SlotRow(row = row, onClick = onSlotClick)
            }
        }
    }
}

// Two, and fixed rather than adaptive, for the same reason the stands grid is: every phone this app
// targets is between 320 and 430dp wide, where a minimum-width grid gives two columns anyway.
private const val CATALOGUE_COLUMNS = 2
