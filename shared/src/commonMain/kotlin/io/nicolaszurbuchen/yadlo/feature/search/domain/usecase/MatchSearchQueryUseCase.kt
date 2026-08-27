package io.nicolaszurbuchen.yadlo.feature.search.domain.usecase

import io.nicolaszurbuchen.yadlo.core.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.feature.search.domain.model.SearchHit
import io.nicolaszurbuchen.yadlo.feature.search.domain.model.SearchIndex
import io.nicolaszurbuchen.yadlo.feature.search.domain.model.SearchResults
import io.nicolaszurbuchen.yadlo.infra.text.foldForSearch

/**
 * One query against the whole Edition.
 *
 * **Substring, folded, over about 120 strings.** No ranking model, no fuzzy distance, no index: the
 * corpus is smaller than one screen of a log file, and the honest bottleneck in a search this size
 * is not the matching but the accents — see `foldForSearch`, which is what makes `preverenges` find
 * *Préverenges*.
 *
 * **A match on the name outranks a match on anything else**, and within each of those the order is
 * alphabetical rather than the content's. A reader who types `sup` and sees *SUP Yoga* third, under
 * two stands whose menus mention it, would reasonably conclude the app did not understand them.
 *
 * **Nothing about the screen the search was opened from reaches here.** The Programme's day and
 * Category chips do not narrow the query, deliberately: a search that silently inherited them would
 * be scoped to the current screen with nothing on the results page saying so, which is the one
 * version of this feature that cannot be recovered from by reading the answer.
 */
class MatchSearchQueryUseCase {
    operator fun invoke(
        index: SearchIndex,
        query: String,
    ): SearchResults {
        val needle = query.trim().foldForSearch()
        if (needle.length < MINIMUM_QUERY_LENGTH) return EMPTY

        val hits = index.happenings.mapNotNull { it.hitFor(needle) }

        return SearchResults(
            // The line the domain already draws between the two: an Activity has hours the
            // organisers set, a Stand is simply there while the site is open.
            programme = hits.filterNot { it.happening is Happening.Stand }.ranked(),
            onSite = hits.filter { it.happening is Happening.Stand }.ranked(),
            topics = index.topics.filter { topic -> topic.keywords.any { needle in it } },
            faq = index.faq.filter { needle in it.question.foldForSearch() || needle in it.answer.foldForSearch() },
        )
    }
}

private fun Happening.hitFor(needle: String): SearchHit? {
    if (needle in name.foldForSearch()) return SearchHit(happening = this, reason = null)

    return matchedDetail(needle)?.let { SearchHit(happening = this, reason = it) }
}

/**
 * The text that matched, when it was not the name — which is what the row writes underneath itself
 * so a result whose title has nothing to do with the query still says why it is there.
 *
 * A dish resolves to its own name rather than to its description, because *Ragoût de tofu* is what
 * the reader was looking for when they typed `tofu`, and the sentence under it on the menu is not.
 *
 * The description is last on purpose: it is the widest net in the index and the least specific
 * thing to say back. Everything above it is a fact about the Happening that a reader would
 * recognise as an answer.
 */
private fun Happening.matchedDetail(needle: String): String? {
    val specific =
        when (this) {
            is Happening.Artist -> {
                genres.firstOrNull { it.matchedBy(needle) }
            }

            is Happening.Activity -> {
                genres.firstOrNull { it.matchedBy(needle) }
                    ?: suitability?.takeIf { it.matchedBy(needle) }
            }

            is Happening.Stand -> {
                offering?.takeIf { it.matchedBy(needle) }
                    ?: menu.firstNotNullOfOrNull { group ->
                        group.items
                            .firstOrNull { it.name.matchedBy(needle) || it.description.matchedBy(needle) }
                            ?.name
                    }
            }
        }

    return specific
        ?: category.name.takeIf { it.matchedBy(needle) }
        ?: description?.takeIf { it.matchedBy(needle) }
}

private fun String?.matchedBy(needle: String): Boolean = this != null && needle in foldForSearch()

private fun List<SearchHit>.ranked(): List<SearchHit> =
    sortedWith(compareBy({ if (it.reason == null) 0 else 1 }, { it.happening.name.foldForSearch() }))

/**
 * One. A corpus of 120 strings can afford to answer the first keystroke — typing `s` narrows to
 * something worth reading rather than to noise — and it is what keeps *aucun résultat* honest:
 * every query that is not empty has genuinely been run.
 */
private const val MINIMUM_QUERY_LENGTH = 1

private val EMPTY =
    SearchResults(programme = emptyList(), onSite = emptyList(), topics = emptyList(), faq = emptyList())
