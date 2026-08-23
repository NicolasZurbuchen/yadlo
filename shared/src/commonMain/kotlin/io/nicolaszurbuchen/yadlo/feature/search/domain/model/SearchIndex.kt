package io.nicolaszurbuchen.yadlo.feature.search.domain.model

import io.nicolaszurbuchen.yadlo.common.content.domain.model.FaqEntry
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening

/**
 * Everything one query can reach, in the shape it is searched in.
 *
 * **Nothing is precomputed, and that is measured rather than lazy.** The 2026 edition is 38
 * Happenings, 62 dishes across 21 menu groups, four questions and fifteen topics — about 120
 * strings, already in memory as the parsed bundle. Folding them on each keystroke is work a phone
 * does between two frames, and an index built ahead of time would be a second copy of the content
 * that can go stale the moment a bundle refreshes mid-festival. SQLDelight FTS was turned down for
 * the same reason: it is slower to build than the search it replaces.
 *
 * [topics] holds only the ones whose content is actually published — an edition with no transport
 * block must not offer *Accès & transports* — so the availability rule lives in
 * `ObserveSearchIndexUseCase` beside the bundle that answers it.
 */
data class SearchIndex(
    val happenings: List<Happening>,
    val topics: List<SearchTopic>,
    val faq: List<FaqEntry>,
)
