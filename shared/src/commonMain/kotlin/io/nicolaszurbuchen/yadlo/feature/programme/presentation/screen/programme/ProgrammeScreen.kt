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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.app.navigation.LocalTabChromeInsets
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component.CatalogueCard
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component.DaySectionHeader
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component.ProgrammeEmptyMessage
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component.ProgrammeHeader
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component.ProgrammeSkeleton
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.component.SlotRow

/**
 * The chrome, then whichever list the selector row is pointing at.
 *
 * Layout B2 — the filters, then one chronological list — over one day or over the whole weekend
 * with a sticky header per day; or the Catalogue: no axis, no headers, and a two-column staggered
 * grid of everything the festival offers.
 *
 * **One screen with two bodies rather than two screens.** The scopes answer questions a visitor
 * moves between in a single thought — "what is there to do" and "when is it on" — and they share
 * the Category filter, the tab, and the fiche every item opens. Splitting them would put a second
 * door onto the same Happenings in the navigation, which is the thing DECISIONS.md refuses twice
 * over; the selector row keeps it one door with several ways to look through it.
 *
 * The chip rows are chrome and stay put while the list scrolls under them — they are how you change
 * what the list is, so scrolling them away would mean scrolling back to change your mind. The day
 * headers are the exception that proves it: they scroll *with* the list, because unlike a filter
 * they are describing what is on screen rather than deciding it.
 */
@Composable
fun ProgrammeScreen(
    state: ProgrammeUiModel,
    onScopeClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onAllCategoriesClick: () -> Unit,
    onSlotClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        ProgrammeSkeleton(modifier = modifier)
        return
    }

    // The shell's bars are drawn over this screen rather than beside it. The header clears the top
    // one here, and the list clears the bottom one in its own content padding.
    val chrome = LocalTabChromeInsets.current

    Column(modifier = modifier.fillMaxSize().padding(top = chrome.top)) {
        ProgrammeHeader(
            scopes = state.scopes,
            categories = state.categories,
            scale = state.scale,
            onScopeClick = onScopeClick,
            onCategoryClick = onCategoryClick,
            onAllCategoriesClick = onAllCategoriesClick,
        )

        if (state.emptyMessage != null) {
            ProgrammeEmptyMessage(message = state.emptyMessage)
            return@Column
        }

        if (state.catalogue.isNotEmpty()) {
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
            state.sections.forEach { section ->
                section.header?.let { header ->
                    stickyHeader(key = section.id) {
                        DaySectionHeader(header = header)
                    }
                }

                itemsIndexed(section.rows, key = { _, row -> row.id }) { index, row ->
                    // A hairline between neighbours, never around each of them: the shared left edge
                    // and the common baseline are the whole reason this is a list of rows, not
                    // cards. Never above the first row of a day either — the header is already the
                    // boundary there, and a rule directly under it would draw it twice.
                    if (index > 0) {
                        HorizontalDivider(color = MaterialTheme.appColors.borderSubtle)
                    }

                    SlotRow(row = row, onClick = onSlotClick)
                }
            }
        }
    }
}

// Two, and fixed rather than adaptive, for the same reason the stands grid is: every phone this app
// targets is between 320 and 430dp wide, where a minimum-width grid gives two columns anyway.
private const val CATALOGUE_COLUMNS = 2
