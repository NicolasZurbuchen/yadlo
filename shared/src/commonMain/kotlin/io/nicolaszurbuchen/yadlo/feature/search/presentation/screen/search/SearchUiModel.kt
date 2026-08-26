package io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search

import io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search.uimodel.SearchTopicUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText

/**
 * One query and what it found, already grouped.
 *
 * [message] is non-null exactly when there are no groups, and it says which kind of nothing: a query
 * too short to mean anything is an invitation, and a query that matched nothing is a fact about the
 * festival. They read differently and a single "aucun résultat" under an empty field would be the
 * app answering a question nobody has asked yet.
 */
data class SearchUiModel(
    val query: String,
    val groups: List<SearchGroupUiModel>,
    val message: UiText?,
)

/**
 * **The groups are what say how wide the search was.** A query typed from the Programme that answers
 * with a *Sur place* heading has demonstrated its own scope, in the place the reader is looking,
 * without a word of explanation — which is the half of the scope problem the placeholder cannot
 * solve on its own.
 *
 * A group with nothing in it is absent rather than empty, the same rule the Programme's day sections
 * follow: three headings with one row between them says less than one heading does.
 */
data class SearchGroupUiModel(
    val id: String,
    val title: UiText,
    val rows: List<SearchRowUiModel>,
)

/**
 * One result. Two shapes, because there are two kinds of thing in this app that can be found and
 * they are honest about being different: a Happening is content and carries its Category, a
 * practical answer is a screen and carries the icon that screen wears everywhere else.
 *
 * They are drawn by one component so they cannot drift apart in the list — see `SearchResultGroup`.
 */
sealed interface SearchRowUiModel {
    val id: String

    /**
     * [reason] is the text that matched when it was not the name — a dish, a genre, the cuisine —
     * so a row whose title has nothing to do with the query still says why it is there. Null when
     * the name matched, because then the title is already the answer.
     */
    data class Happening(
        override val id: String,
        val happeningId: String,
        val name: String,
        val categoryId: String,
        val categoryName: String,
        val reason: String?,
    ) : SearchRowUiModel

    /**
     * A screen, and the FAQ questions are these too: a question is titled in the content's own words
     * and opens the page that answers it. There is no screen for a single question, so a result that
     * pretended otherwise would dead-end — the same rule that resolves a dish to its stand.
     */
    data class Practical(
        override val id: String,
        val topic: SearchTopicUiModel,
        val title: UiText,
    ) : SearchRowUiModel
}
