package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.nicolaszurbuchen.yadlo.app.design.component.YadloSectionHeader
import io.nicolaszurbuchen.yadlo.app.design.component.YadloTopAppBar
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.common.content.presentation.component.StandCard
import io.nicolaszurbuchen.yadlo.infra.ui.asString
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
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize().padding(contentPadding),
                ) {
                    CircularProgressIndicator()
                }
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
                LazyVerticalGrid(
                    columns = GridCells.Fixed(COLUMNS),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
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
                        item(key = group.id, span = { GridItemSpan(maxLineSpan) }) {
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
