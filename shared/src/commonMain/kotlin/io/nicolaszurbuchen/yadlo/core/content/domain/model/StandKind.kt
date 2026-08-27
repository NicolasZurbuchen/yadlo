package io.nicolaszurbuchen.yadlo.core.content.domain.model

/**
 * Which half of the stands is being asked for.
 *
 * The two Categories a Stand may legally carry, and the app names them because SCHEMA.md closes the
 * set: `kind: "stand"` admits `restauration` and `createurs` and nothing else, and the validator
 * enforces it. A third would be a content decision that arrives with a screen anyway.
 *
 * They are two entries on the tab rather than one list with a header each, because nobody looking
 * for dinner is also browsing for a second-hand costume. The Wishlist still groups them together —
 * there they are what one person kept, and the axis that matters is what they were saved from.
 */
enum class StandKind(
    val categoryId: String,
) {
    FOOD("restauration"),
    MAKERS("createurs"),
}
