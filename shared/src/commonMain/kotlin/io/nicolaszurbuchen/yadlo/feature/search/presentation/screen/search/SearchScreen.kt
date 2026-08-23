package io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.theme.spacing
import io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.component.SearchResultGroup
import io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.component.SearchTopBar
import io.nicolaszurbuchen.yadlo.infra.ui.asString

/**
 * The field, and whatever it found.
 *
 * There is exactly one of a message and a list on screen, because [SearchUiModel.message] is
 * non-null precisely when there are no groups — an empty list under an invitation would be the
 * screen saying the same thing twice, once in words and once in silence.
 */
@Composable
fun SearchScreen(
    state: SearchUiModel,
    onQueryChange: (String) -> Unit,
    onHappeningClick: (String) -> Unit,
    onTopicClick: (SearchTopicUiModel) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            SearchTopBar(
                query = state.query,
                onQueryChange = onQueryChange,
                onBackClick = onBackClick,
            )
        },
        modifier = modifier,
    ) { contentPadding ->
        val message = state.message

        if (message != null) {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxSize().padding(contentPadding),
            ) {
                Text(
                    text = message.asString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.appColors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(MaterialTheme.spacing.xl),
                )
            }

            return@Scaffold
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
            contentPadding =
                PaddingValues(
                    start = MaterialTheme.spacing.md,
                    end = MaterialTheme.spacing.md,
                    top = MaterialTheme.spacing.md,
                    bottom = MaterialTheme.spacing.xl,
                ),
            modifier = Modifier.fillMaxSize().padding(contentPadding),
        ) {
            items(state.groups, key = { it.id }) { group ->
                SearchResultGroup(
                    group = group,
                    onHappeningClick = onHappeningClick,
                    onTopicClick = onTopicClick,
                )
            }
        }
    }
}
