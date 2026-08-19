package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import io.nicolaszurbuchen.yadlo.app.design.theme.ShimmerPulse
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.component.PlusScreenScaffold
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.component.StandMarkChips
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.component.StandRow
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
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.sm),
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
                            .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                contentPadding = PaddingValues(vertical = MaterialTheme.spacing.sm, horizontal = MaterialTheme.spacing.md),
                modifier = Modifier.fillMaxSize().padding(contentPadding),
            ) {
                state.emptyMessage?.let { message ->
                    item(key = EMPTY_KEY) {
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
                    StandRow(stand = stand, onClick = onStandClick)
                }
            }
        }
    }
}

private const val EMPTY_KEY = "empty"
