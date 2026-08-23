package io.nicolaszurbuchen.yadlo.feature.search.domain.model

import io.nicolaszurbuchen.yadlo.common.content.domain.model.FaqEntry

/**
 * What one query found, already split into the groups the screen draws.
 *
 * **Grouped here rather than in the mapper**, because the split is the same one the domain already
 * makes everywhere else: [programme] is what the festival programmed and [onSite] is what is simply
 * there while the site is open, which is the line `Happening.Activity`'s own documentation draws
 * between an Activity and a Stand.
 *
 * The groups are also what demonstrates the scope. A search run from the Programme tab that answers
 * with a *Sur place* heading has shown the reader it searched everything, in the one place they are
 * definitely looking — which is what a magnifier in a shared toolbar cannot say on its own.
 */
data class SearchResults(
    val programme: List<SearchHit>,
    val onSite: List<SearchHit>,
    val topics: List<SearchTopic>,
    val faq: List<FaqEntry>,
) {
    val isEmpty: Boolean
        get() = programme.isEmpty() && onSite.isEmpty() && topics.isEmpty() && faq.isEmpty()
}
