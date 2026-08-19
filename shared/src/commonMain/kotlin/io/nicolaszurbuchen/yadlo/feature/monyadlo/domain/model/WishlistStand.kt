package io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.model

import io.nicolaszurbuchen.yadlo.common.content.domain.model.DietaryCoverage

/**
 * One Stand on the Wishlist.
 *
 * No hours and no live state — DECISIONS.md § No opening times on the Wishlist. Whether a stand
 * closes before the festival does is unknown, and "ouvert maintenant" is too good a claim to make on
 * data nobody has.
 *
 * [offering] and [dietary] are what the row shows under the name: "Cuisine végétale", then what the
 * stand can feed you, derived from its menu. Both describe the whole stand, which is the unit that
 * gets saved.
 */
data class WishlistStand(
    val id: String,
    val name: String,
    val offering: String?,
    val dietary: Map<String, DietaryCoverage>,
)
