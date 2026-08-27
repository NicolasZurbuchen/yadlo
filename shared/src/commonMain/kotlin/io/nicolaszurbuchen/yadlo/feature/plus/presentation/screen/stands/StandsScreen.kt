package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import io.nicolaszurbuchen.yadlo.core.content.presentation.component.StandCard
import io.nicolaszurbuchen.yadlo.design.theme.ShimmerPulse
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusScreenScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.component.StandMarkChips
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.component.StandsSkeleton
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * One half of the stands: *Nourriture & boissons*, or *Créateurs*.
 *
 * The browse half of the pair the Wishlist recalls: this is where a stand is discovered, and its
 * fiche is where it is kept. Neither half offers the other's job, which is what keeps "one place to
 * find a thing, one place to see what you kept" true across the app.
 *
 * **Two entries rather than one list with two headers.** Nobody looking for dinner is also browsing
 * for a second-hand costume, so the Category is chosen on the tab and never appears again here. The
 * Wishlist still groups them together — there they are what one person kept, and what they were
 * saved from is the axis that matters.
 *
 * **The chips are part of the bar, not the first row of the list.** They were an `item` in the
 * LazyColumn, which meant the reason someone with a dietary requirement opened this screen scrolled
 * away the moment they started reading — and a filter you have to scroll back up to change is a
 * filter that gets used once. *Créateurs* publishes no marks, so it simply has none.
 *
 * The body is lazy and keyed rather than one scrolling column, which is why this wears
 * [PlusScreenScaffold] directly instead of [PlusDetailScaffold] like the prose screens.
 */
@Composable
fun StandsScreen(
    state: StandsUiModel,
    onBackClick: () -> Unit,
    onMarkClick: (String?) -> Unit,
    onStandClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    PlusScreenScaffold(
        title = state.title.asString(),
        onBackClick = onBackClick,
        modifier = modifier,
        underBar = {
            // One chip is *Tout* alone, which filters nothing and only takes up the room the first
            // stand should have. That is the Créateurs case exactly.
            if (state.chips.size > 1) {
                // Vertical only. The horizontal inset belongs inside the scroll, where the chips
                // can carry it past both screen edges — see StandMarkChips.
                StandMarkChips(
                    chips = state.chips,
                    onMarkClick = onMarkClick,
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.sm),
                )
            }
        },
    ) { contentPadding ->
        if (state.isLoading) {
            ShimmerPulse {
                StandsSkeleton(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(contentPadding)
                            .padding(MaterialTheme.spacing.md),
                )
            }
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(COLUMNS),
                // Equal and tight both ways, which is what makes a grid read as a grid rather than
                // as two lists: a gutter wider than the gap between two cards separates the columns
                // more than it separates the cards inside one.
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                verticalItemSpacing = MaterialTheme.spacing.sm,
                contentPadding = PaddingValues(MaterialTheme.spacing.md),
                modifier = Modifier.fillMaxSize().padding(contentPadding),
            ) {
                state.emptyMessage?.let { message ->
                    // Across both columns. A sentence explaining why the list is empty, set in one
                    // half of the screen with a hole beside it, reads as a card that failed.
                    item(key = EMPTY_KEY, span = StaggeredGridItemSpan.FullLine) {
                        Text(
                            text = message.asString(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.appColors.textSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.xl),
                        )
                    }
                }

                items(items = state.stands, key = { it.id }) { stand ->
                    StandCard(stand = stand, onClick = onStandClick)
                }
            }
        }
    }
}

private const val EMPTY_KEY = "empty"

// Two, and fixed rather than adaptive. Every phone this app targets is between 320 and 430dp wide,
// where a minimum-width grid would give two columns anyway and a tablet is not a target — an
// adaptive count would be machinery answering a question nobody is asking yet.
private const val COLUMNS = 2
