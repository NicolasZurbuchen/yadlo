package io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search

import io.nicolaszurbuchen.yadlo.feature.search.domain.model.SearchIndex
import io.nicolaszurbuchen.yadlo.feature.search.domain.model.SearchResults
import io.nicolaszurbuchen.yadlo.feature.search.domain.model.SearchTopic
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SearchReducerTest {
    @Test
    fun reduce_initially_hasNoQueryAndNothingToSearch() {
        val state = SearchState()

        assertEquals("", state.query)
        assertNull(state.index)
        assertNull(state.results)
        assertTrue(state.topics.isEmpty())
    }

    @Test
    fun reduce_indexUpdated_holdsIt() {
        val index = SearchIndex(happenings = emptyList(), topics = listOf(SearchTopic.PAYMENT), faq = emptyList())

        val state = reduce(SearchState(), SearchMessage.IndexUpdated(index))

        assertEquals(index, state.index)
    }

    @Test
    fun reduce_indexUpdated_leavesTheQueryAlone() {
        // A bundle refreshing mid-festival must not clear a field somebody is typing into.
        val state = reduce(SearchState(query = "twint"), SearchMessage.IndexUpdated(emptyIndex()))

        assertEquals("twint", state.query)
    }

    @Test
    fun reduce_queryChanged_holdsIt() {
        val state = reduce(SearchState(), SearchMessage.QueryChanged("sup"))

        assertEquals("sup", state.query)
    }

    @Test
    fun reduce_queryChanged_leavesTheIndexAlone() {
        val index = emptyIndex()

        val state = reduce(SearchState(index = index), SearchMessage.QueryChanged("sup"))

        assertEquals(index, state.index)
    }

    @Test
    fun reduce_queryCleared_isJustAnotherQuery() {
        val state = reduce(SearchState(query = "sup"), SearchMessage.QueryChanged(""))

        assertEquals("", state.query)
    }

    @Test
    fun reduce_resultsUpdated_holdsBothTheResultsAndTheirConvertedTopics() {
        val results = results(topics = listOf(SearchTopic.PAYMENT))

        val state =
            reduce(
                SearchState(),
                SearchMessage.ResultsUpdated(results = results, topics = listOf(SearchTopicUiModel.PAYMENT)),
            )

        assertEquals(results, state.results)
        assertEquals(listOf(SearchTopicUiModel.PAYMENT), state.topics)
    }

    @Test
    fun reduce_resultsUpdated_replacesTheLastOnesRatherThanAddingToThem() {
        val previous =
            SearchState(
                results = results(topics = listOf(SearchTopic.PAYMENT)),
                topics = listOf(SearchTopicUiModel.PAYMENT),
            )

        val state =
            reduce(
                previous,
                SearchMessage.ResultsUpdated(results = results(), topics = emptyList()),
            )

        assertTrue(state.results?.isEmpty == true)
        assertTrue(state.topics.isEmpty())
    }

    private fun reduce(
        state: SearchState,
        message: SearchMessage,
    ): SearchState = with(SearchStoreFactory.ReducerImpl) { state.reduce(message) }

    private fun emptyIndex() = SearchIndex(happenings = emptyList(), topics = emptyList(), faq = emptyList())

    private fun results(topics: List<SearchTopic> = emptyList()) =
        SearchResults(programme = emptyList(), onSite = emptyList(), topics = topics, faq = emptyList())
}
