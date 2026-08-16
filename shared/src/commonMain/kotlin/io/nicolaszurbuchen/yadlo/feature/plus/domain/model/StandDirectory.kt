package io.nicolaszurbuchen.yadlo.feature.plus.domain.model

/**
 * One half of the stands — *Nourriture & boissons* or *Créateurs* — and the marks worth offering as
 * a filter over it.
 *
 * A flat list rather than groups: the entry that opened it already named the Category, so a header
 * repeating it would be the screen saying its own title back. The Wishlist is the one that groups,
 * because there the two arrive mixed.
 *
 * [marks] is derived from the listing rather than declared in Kotlin, so a chip is never shown that
 * matches nothing, and a mark the content adds appears without an app release. Deciding here which
 * of the six published marks count as "dietary" would be duplicating a content decision in code —
 * `piquant` is exactly as much a reason to avoid a stand as `sans gluten` is a reason to choose one.
 */
data class StandDirectory(
    val stands: List<StandListing>,
    val marks: List<String>,
)
