package io.nicolaszurbuchen.yadlo.feature.plus.domain.model

/**
 * The Stands of one Category. Same grouping axis as the Wishlist and the Programme's filter chips,
 * so *restauration* and *créateurs* mean one thing across the app rather than being a second
 * taxonomy grown for this screen.
 */
data class StandGroup(
    val categoryId: String,
    val categoryName: String,
    val stands: List<StandListing>,
)
