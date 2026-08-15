package io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.model

/**
 * One Stand on the Wishlist.
 *
 * No hours and no live state — DECISIONS.md § No opening times on the Wishlist. Whether a stand
 * closes before the festival does is unknown, and "ouvert maintenant" is too good a claim to make on
 * data nobody has.
 *
 * [offering] and [marks] are what the row shows under the name: "Cuisine végétale", then `végan` and
 * `bio`. They describe the whole stand, which is the unit that gets saved.
 */
data class WishlistStand(
    val id: String,
    val name: String,
    val offering: String?,
    val marks: List<String>,
)
