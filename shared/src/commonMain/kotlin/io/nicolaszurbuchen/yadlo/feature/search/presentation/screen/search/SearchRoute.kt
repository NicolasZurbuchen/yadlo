package io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.mapper.toDomain
import io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.mapper.toUiModel
import io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.uimodel.SearchTopicUiModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchRoute(
    onNavigateToHappening: (String) -> Unit,
    onNavigateToTopic: (SearchTopicUiModel) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onNavigateToHappeningUpdated by rememberUpdatedState(onNavigateToHappening)
    val onNavigateToTopicUpdated by rememberUpdatedState(onNavigateToTopic)

    LaunchedEffect(Unit) {
        viewModel.labels.collect { label ->
            when (label) {
                is SearchLabel.NavigateToHappening -> onNavigateToHappeningUpdated(label.happeningId)
                is SearchLabel.NavigateToTopic -> onNavigateToTopicUpdated(label.topic.toUiModel())
            }
        }
    }

    SearchScreen(
        state = state,
        onQueryChange = { query -> viewModel.onIntent(SearchIntent.QueryChanged(query)) },
        onHappeningClick = { happeningId -> viewModel.onIntent(SearchIntent.HappeningClicked(happeningId)) },
        onTopicClick = { topic -> viewModel.onIntent(SearchIntent.TopicClicked(topic.toDomain())) },
        onBackClick = onNavigateBack,
        modifier = modifier,
    )
}
