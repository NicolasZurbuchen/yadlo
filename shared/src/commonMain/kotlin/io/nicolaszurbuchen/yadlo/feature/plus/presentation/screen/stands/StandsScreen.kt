package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.component.StandMarkChips
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.component.StandRow
import io.nicolaszurbuchen.yadlo.infra.ui.asString
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.plus_back

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
 * The chips stay above the list rather than scrolling away with it. They are the reason someone
 * with a dietary requirement opened this screen at all, and a filter you have to scroll back up to
 * change is a filter that gets used once. *Créateurs* publishes no marks, so it simply has none.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandsScreen(
    state: StandsUiModel,
    onBackClick: () -> Unit,
    onMarkClick: (String?) -> Unit,
    onStandClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.title.asString(),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.plus_back),
                        )
                    }
                },
            )
        },
        modifier = modifier,
    ) { contentPadding ->
        if (state.isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize().padding(contentPadding),
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                contentPadding = PaddingValues(vertical = MaterialTheme.spacing.sm, horizontal = MaterialTheme.spacing.md),
                modifier = Modifier.fillMaxSize().padding(contentPadding),
            ) {
                if (state.chips.size > 1) {
                    // One chip is *Tout* alone, which filters nothing and only takes up the room
                    // the first stand should have. That is the Créateurs case exactly.
                    item(key = CHIPS_KEY) {
                        StandMarkChips(chips = state.chips, onMarkClick = onMarkClick)
                    }
                }

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

private const val CHIPS_KEY = "chips"
private const val EMPTY_KEY = "empty"
