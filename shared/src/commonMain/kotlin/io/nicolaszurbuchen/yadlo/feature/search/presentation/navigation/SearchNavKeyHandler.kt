package io.nicolaszurbuchen.yadlo.feature.search.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.SearchRoute
import io.nicolaszurbuchen.yadlo.infra.navigation.NavKeyHandler

class SearchNavKeyHandler(
    private val navigator: SearchNavigator,
) : NavKeyHandler {
    override fun EntryProviderScope<NavKey>.registerEntries() {
        entry<SearchDestination> {
            SearchRoute(
                onNavigateToHappening = { id -> navigator.navigateToHappening(id) },
                onNavigateToTopic = { topic -> navigator.navigateToTopic(topic) },
                onNavigateBack = { navigator.navigateBack() },
            )
        }
    }
}
