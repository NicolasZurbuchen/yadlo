package io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search

import io.nicolaszurbuchen.yadlo.feature.search.domain.model.SearchIndex
import io.nicolaszurbuchen.yadlo.feature.search.domain.model.SearchResults

sealed interface SearchIntent {
    data class QueryChanged(
        val query: String,
    ) : SearchIntent

    /**
     * A result lands on the Happening's fiche, whichever group it was in and whatever matched it —
     * an artist, an activity, a stand. There is no screen for a dish and none for a single Slot.
     */
    data class HappeningClicked(
        val happeningId: String,
    ) : SearchIntent

    data class TopicClicked(
        val topic: SearchTopicUiModel,
    ) : SearchIntent
}

sealed interface SearchLabel {
    data class NavigateToHappening(
        val happeningId: String,
    ) : SearchLabel

    data class NavigateToTopic(
        val topic: SearchTopicUiModel,
    ) : SearchLabel
}

sealed interface SearchAction {
    data object ObserveIndex : SearchAction
}

sealed interface SearchMessage {
    data class IndexUpdated(
        val index: SearchIndex,
    ) : SearchMessage

    data class QueryChanged(
        val query: String,
    ) : SearchMessage

    data class ResultsUpdated(
        val results: SearchResults,
    ) : SearchMessage
}

/**
 * **No clock, and no filters carried in from anywhere.** This is the one screen in the app with
 * nothing live on it: a result is a thing rather than an occurrence, so there is no pill to age and
 * no bar to redraw. And nothing about the tab the search was opened from reaches here — the
 * Programme's day and Category chips do not narrow the query, deliberately, because a search that
 * silently inherited them would be scoped to the screen behind it with nothing on the page saying
 * so.
 *
 * [results] is null until the first bundle arrives, which is also the only state in which the screen
 * cannot answer at all.
 */
data class SearchState(
    val query: String = "",
    val index: SearchIndex? = null,
    val results: SearchResults? = null,
)
