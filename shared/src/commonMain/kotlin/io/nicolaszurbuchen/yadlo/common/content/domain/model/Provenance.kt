package io.nicolaszurbuchen.yadlo.common.content.domain.model

/** How trustworthy a piece of curated content is. Prices above all. */
enum class Provenance {
    CONFIRMED,

    /** Taken from a past Edition's record rather than confirmed fresh. */
    ARCHIVED,

    /** Reconstructed or derived. Never presented as fact without a caveat. */
    UNVERIFIED,
}
