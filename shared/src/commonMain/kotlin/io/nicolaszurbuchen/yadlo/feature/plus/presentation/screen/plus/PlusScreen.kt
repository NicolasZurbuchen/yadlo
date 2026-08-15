package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.component.PlusCard
import org.jetbrains.compose.resources.stringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.tab_plus

/**
 * *Plus* — the permanent home of everything the festival is that is not its programme.
 *
 * An iOS-style grouped list, and the one screen in the app that is mostly a table of contents.
 * Accueil borrows individual entries from it by Phase, so the institutional and call-to-action
 * material surfaces in March rather than sitting unread in July; this is where all of it lives the
 * rest of the year.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlusScreen(
    state: PlusUiModel,
    onEntryClick: (PlusEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.tab_plus),
                        style = MaterialTheme.typography.headlineLarge,
                    )
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
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
                contentPadding = PaddingValues(bottom = MaterialTheme.spacing.xl),
                modifier = Modifier.fillMaxSize().padding(contentPadding),
            ) {
                items(state.groups, key = { it.id.name }) { group ->
                    PlusCard(group = group, onEntryClick = onEntryClick)
                }
            }
        }
    }
}
