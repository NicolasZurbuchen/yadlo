package io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.model

/**
 * The tab's two halves: the timeline, and the one number the Wishlist tile writes.
 *
 * [wishlistCount] rather than the Stands themselves. The tile says how many there are and opens the
 * screen that lists them — DECISIONS.md § Two verbs — so carrying the full list here would build the
 * other screen twice and rebuild it on every tick of this one.
 */
data class MonYadloContent(
    val days: List<PlannedDay>,
    val wishlistCount: Int,
)
