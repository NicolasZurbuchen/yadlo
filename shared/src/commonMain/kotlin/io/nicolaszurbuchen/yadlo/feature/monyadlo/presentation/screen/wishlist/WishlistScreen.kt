package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.core.content.presentation.component.StandCard
import io.nicolaszurbuchen.yadlo.design.component.YadloSectionHeader
import io.nicolaszurbuchen.yadlo.design.component.YadloTopAppBar
import io.nicolaszurbuchen.yadlo.design.theme.appColors
import io.nicolaszurbuchen.yadlo.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist.component.WishlistSkeleton
import io.nicolaszurbuchen.yadlo.infra.text.asString
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.wishlist_title

/**
 * *À essayer* — the saved Stands and nothing else.
 *
 * A checklist, not a schedule: nothing here has a time and nothing reminds you of anything. It is
 * also the whole screen — no browse, no search, no `+` — because discovering a stand happens in
 * Plus › Nourriture & boissons, and one place to browse a thing plus one place to see what you kept
 * is the same rule the LIVE Accueil follows.
 *
 * **Two columns, and the same cards Plus draws.** Both halves of that matter: what you kept has to
 * look like what you were looking at when you kept it, down to the number of columns, or the two
 * screens read as two different inventories of the same eight stands.
 */
@Composable
fun WishlistScreen(
    state: WishlistUiModel,
    onBackClick: () -> Unit,
    onStandClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            YadloTopAppBar(
                title = stringResource(Res.string.wishlist_title),
                onBackClick = onBackClick,
            )
        },
        modifier = modifier,
    ) { contentPadding ->
        when {
            state.isLoading -> {
                WishlistSkeleton(modifier = Modifier.padding(contentPadding))
            }

            state.emptyMessage != null -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize().padding(contentPadding).padding(MaterialTheme.spacing.xl),
                ) {
                    Text(
                        text = state.emptyMessage.asString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.appColors.textSecondary,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            else -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(COLUMNS),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                    verticalItemSpacing = MaterialTheme.spacing.sm,
                    contentPadding =
                        PaddingValues(
                            start = MaterialTheme.spacing.md,
                            top = contentPadding.calculateTopPadding() + MaterialTheme.spacing.md,
                            end = MaterialTheme.spacing.md,
                            bottom = contentPadding.calculateBottomPadding() + MaterialTheme.spacing.md,
                        ),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    // One grid with headers across it rather than a column of blocks each holding
                    // its own grid: two Categories laid out separately would each round their last
                    // row up to two columns, so a group of three would leave a hole beside the
                    // third card — and the grid alignment would be the only thing saying so.
                    state.groups.forEachIndexed { index, group ->
                        // A full line, which on a staggered grid also levels the two columns:
                        // a Category header cannot sit halfway up one of them.
                        item(key = group.id, span = StaggeredGridItemSpan.FullLine) {
                            YadloSectionHeader(
                                title = group.name,
                                // Categories are further apart than the cards inside one, or
                                // *Créateurs* reads as the third stand in *Restauration*. The first
                                // header has the grid's own top padding above it already.
                                modifier =
                                    Modifier.padding(
                                        top = if (index > 0) MaterialTheme.spacing.md else NO_PADDING,
                                    ),
                            )
                        }

                        items(items = group.stands, key = { it.id }) { stand ->
                            StandCard(stand = stand, onClick = onStandClick)
                        }
                    }
                }
            }
        }
    }
}

/** The browse lists' own grid — see StandsScreen, where the count is argued. */
private const val COLUMNS = 2

/** Only the first header goes without one, and `0.dp` at a call site is a magic number. */
private val NO_PADDING = 0.dp
