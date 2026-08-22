package io.nicolaszurbuchen.yadlo.feature.happening.domain.model

/**
 * Which of the three a fiche is showing.
 *
 * **The fiche itself deliberately does not use this**, and that has not changed: every section it
 * draws is decided by whether the content behind it exists — a price, a menu, a heart — rather than
 * by asking what kind of thing this is, which is what lets one screen serve all three without a
 * branch per section.
 *
 * It exists for the share message, which is the one thing on this screen that genuinely cannot be
 * derived from the content: *viens voir ce concert* and *je te partage ce stand* are different
 * sentences about the same shape of data, and no field on [HappeningDetail] distinguishes them.
 * Deriving it from a proxy — a non-null heart, a non-empty menu — would be encoding the kind
 * anyway, in a place nobody would look for it.
 */
enum class HappeningKind {
    ARTIST,
    ACTIVITY,
    STAND,
}
