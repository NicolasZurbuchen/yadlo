package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.component.PlusCard

/**
 * *Plus* — the permanent home of everything the festival is that is not its programme.
 *
 * An iOS-style grouped list, and the one screen in the app that is mostly a table of contents.
 * Accueil borrows individual entries from it by Phase, so the institutional and call-to-action
 * material surfaces in March rather than sitting unread in July; this is where all of it lives the
 * rest of the year.
 *
 * **No bar of its own.** This is a tab root, and the shell already draws one — the festival's name
 * and the edition's dates, on every root. A second bar underneath saying "Plus" would repeat what
 * the selected tab in the bottom bar has just said, and cost a fifth of the screen to do it.
 */
@Composable
fun PlusScreen(
    state: PlusUiModel,
    onEntryClick: (PlusEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.fillMaxSize(),
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
            contentPadding =
                PaddingValues(
                    start = MaterialTheme.spacing.md,
                    end = MaterialTheme.spacing.md,
                    top = MaterialTheme.spacing.md,
                    bottom = MaterialTheme.spacing.xl,
                ),
            modifier = modifier.fillMaxSize(),
        ) {
            items(state.groups, key = { it.id.name }) { group ->
                PlusCard(group = group, onEntryClick = onEntryClick)
            }
        }
    }
}
