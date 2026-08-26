package io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.feature.search.domain.model.SearchIndex
import io.nicolaszurbuchen.yadlo.feature.search.domain.usecase.MatchSearchQueryUseCase
import io.nicolaszurbuchen.yadlo.feature.search.domain.usecase.ObserveSearchIndexUseCase
import kotlinx.coroutines.launch

interface SearchStore : Store<SearchIntent, SearchState, SearchLabel>

class SearchStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeSearchIndex: ObserveSearchIndexUseCase,
    private val matchSearchQuery: MatchSearchQueryUseCase,
) {
    fun create(): SearchStore =
        object :
            SearchStore,
            Store<SearchIntent, SearchState, SearchLabel> by storeFactory.create(
                name = "SearchStore",
                initialState = SearchState(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<SearchAction>() {
        override fun invoke() {
            dispatch(SearchAction.ObserveIndex)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<SearchIntent, SearchAction, SearchState, SearchMessage, SearchLabel>() {
        override fun executeAction(action: SearchAction) {
            when (action) {
                SearchAction.ObserveIndex -> observeIndex()
            }
        }

        override fun executeIntent(intent: SearchIntent) {
            when (intent) {
                is SearchIntent.QueryChanged -> {
                    dispatch(SearchMessage.QueryChanged(intent.query))
                    match()
                }

                is SearchIntent.HappeningClicked -> {
                    publish(SearchLabel.NavigateToHappening(intent.happeningId))
                }

                is SearchIntent.TopicClicked -> {
                    publish(SearchLabel.NavigateToTopic(intent.topic))
                }
            }
        }

        /**
         * Re-running the query on every bundle is what makes a correction pushed mid-festival show
         * up under a field somebody is already looking at, rather than the next time they type.
         */
        private fun observeIndex() {
            scope.launch {
                observeSearchIndex().collect { index ->
                    dispatch(SearchMessage.IndexUpdated(index))
                    match()
                }
            }
        }

        /**
         * Synchronous, and on every keystroke. There is no debounce because there is nothing to
         * debounce: this is a substring scan of about 120 strings already in memory, and a delay
         * would be latency the app chose to add — see [SearchIndex] for why nothing is precomputed.
         */
        private fun match() {
            val index = state().index ?: return
            val results = matchSearchQuery(index = index, query = state().query)

            dispatch(SearchMessage.ResultsUpdated(results))
        }
    }

    // internal (not private) so SearchReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<SearchState, SearchMessage> {
        override fun SearchState.reduce(msg: SearchMessage): SearchState =
            when (msg) {
                is SearchMessage.IndexUpdated -> {
                    copy(index = msg.index)
                }

                is SearchMessage.QueryChanged -> {
                    copy(query = msg.query)
                }

                is SearchMessage.ResultsUpdated -> {
                    copy(results = msg.results)
                }
            }
    }
}
