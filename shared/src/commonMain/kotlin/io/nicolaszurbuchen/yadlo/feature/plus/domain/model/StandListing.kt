package io.nicolaszurbuchen.yadlo.feature.plus.domain.model

/**
 * One Stand on *Nourriture & boissons* — the browse half of the pair whose recall half is the
 * Wishlist. Opening it opens the same fiche, which is where the menu and the one heart live.
 *
 * **No opening hours.** The prototype wrote them on every row and they are the honest omission
 * here: not one of the six stands publishes any, so a time on this row would be invented. It is
 * also the single most useful thing the association could send — see content/GAPS.md.
 *
 * [marks] are the Stand's own, describing everything it sells. [dietaryMatches] is wider: it also
 * holds the marks that only some of its dishes carry, because "can I eat here" is answered by
 * either. The row shows the first; the filter uses the second.
 */
data class StandListing(
    val id: String,
    val name: String,
    val offering: String?,
    val marks: List<String>,
    val dietaryMatches: Set<String>,
)
