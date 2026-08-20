package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import io.nicolaszurbuchen.yadlo.app.design.component.YadloTopAppBar
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist.component.WishlistGroupBlock
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
                LazyColumn(
                    // Between two Categories, not between two cards: the cards inside a block carry
                    // their own gap, and one size for both would make *Créateurs* look like the
                    // third stand in *Restauration*.
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
                    contentPadding =
                        PaddingValues(
                            top = contentPadding.calculateTopPadding() + MaterialTheme.spacing.md,
                            bottom = contentPadding.calculateBottomPadding() + MaterialTheme.spacing.md,
                        ),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(state.groups, key = { it.id }) { group ->
                        WishlistGroupBlock(group = group, onStandClick = onStandClick)
                    }
                }
            }
        }
    }
}
