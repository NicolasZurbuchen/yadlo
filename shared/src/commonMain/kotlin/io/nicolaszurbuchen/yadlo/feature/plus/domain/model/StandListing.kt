package io.nicolaszurbuchen.yadlo.feature.plus.domain.model

import io.nicolaszurbuchen.yadlo.core.content.domain.model.DietaryCoverage

/**
 * One Stand on *Nourriture & boissons* — the browse half of the pair whose recall half is the
 * Wishlist. Opening it opens the same fiche, which is where the menu and the one heart live.
 *
 * **No opening hours.** The prototype wrote them on every row and they are the honest omission
 * here: not one of the six stands publishes any, so a time on this row would be invented. It is
 * also the single most useful thing the association could send — see content/GAPS.md.
 *
 * [dietary] is what this stand can feed you, derived from its own menu: whether a mark covers
 * everything it sells or only part of it. The card shows it, and the filter answers with it, so a
 * stand can never match a chip and then fail to say why.
 *
 * [imageUrl] is the first photograph the content declares, which is all eight of them have. It is
 * null for a Stand added before its picture arrived, and the card falls back to the bundled one
 * rather than to a hole.
 */
data class StandListing(
    val id: String,
    val name: String,
    val offering: String?,
    val imageUrl: String?,
    val dietary: Map<String, DietaryCoverage>,
)
