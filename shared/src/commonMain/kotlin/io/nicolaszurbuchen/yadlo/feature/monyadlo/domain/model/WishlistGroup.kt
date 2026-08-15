package io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.model

/**
 * The saved Stands of one Category — the grouping DECISIONS.md § Two verbs asks the Wishlist screen
 * for.
 *
 * Category is the only grouping axis in the app, so this is the same axis the Programme filters on
 * rather than a second taxonomy invented for one screen. Today's content declares two that Stands
 * use, `restauration` and `createurs`; the shape does not change when there are four.
 */
data class WishlistGroup(
    val categoryId: String,
    val categoryName: String,
    val stands: List<WishlistStand>,
)
