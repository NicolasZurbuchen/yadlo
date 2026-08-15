package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist.component.WishlistGroupBlock
import io.nicolaszurbuchen.yadlo.infra.ui.asString
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.wishlist_back
import yadlo.shared.generated.resources.wishlist_title

/**
 * *À essayer* — the saved Stands and nothing else.
 *
 * A checklist, not a schedule: nothing here has a time and nothing reminds you of anything. It is
 * also the whole screen — no browse, no search, no `+` — because discovering a stand happens in
 * Plus › Nourriture & boissons, and one place to browse a thing plus one place to see what you kept
 * is the same rule the LIVE Accueil follows.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    state: WishlistUiModel,
    onBackClick: () -> Unit,
    onStandClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.wishlist_title),
                        // Set explicitly: TopAppBar defaults its title to titleLarge, which in this
                        // project is the button-label role rather than a heading.
                        style = MaterialTheme.typography.headlineSmall,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.wishlist_back),
                        )
                    }
                },
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
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
                    contentPadding = contentPadding,
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
