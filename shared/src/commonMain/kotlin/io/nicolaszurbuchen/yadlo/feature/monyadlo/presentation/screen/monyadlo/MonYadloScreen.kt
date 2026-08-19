package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.app.navigation.tabContentPadding
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo.component.PlannedDayBlock
import io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo.component.WishlistTile
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * The tab: one tile for *à essayer*, then the Plan as a rail of days.
 *
 * **Recall only** — DECISIONS.md § Mon Yadlo never browses. There is no add-flow and no search here;
 * a row opens the fiche it came from, which is also the one place it can be taken off the Plan
 * again, so the same thing never carries two hearts.
 */
@Composable
fun MonYadloScreen(
    state: MonYadloUiModel,
    onSlotClick: (String) -> Unit,
    onWishlistClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
        // The shell's bars are drawn over this list rather than beside it, so their height is part
        // of the padding — see tabContentPadding.
        contentPadding = tabContentPadding(top = MaterialTheme.spacing.md, bottom = MaterialTheme.spacing.md),
        modifier = modifier.fillMaxSize(),
    ) {
        item(key = "wishlist") {
            WishlistTile(
                count = state.wishlistCount,
                onClick = onWishlistClick,
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
            )
        }

        state.emptyMessage?.let { message ->
            item(key = "empty") {
                Text(
                    text = message.asString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.appColors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.xl, vertical = MaterialTheme.spacing.xxl),
                )
            }
        }

        items(state.days, key = { it.id }) { day ->
            PlannedDayBlock(day = day, onRowClick = onSlotClick)
        }
    }
}
