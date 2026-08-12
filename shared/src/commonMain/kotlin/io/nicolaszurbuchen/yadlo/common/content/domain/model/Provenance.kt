package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * How trustworthy a piece of curated content is.
 *
 * Carried by anything reconstructed rather than given — prices above all. The app is built by one
 * person transcribing an association's Instagram, so "we believe this" and "they told us this" are
 * genuinely different claims and the UI is allowed to say which it is holding.
 */
enum class Provenance {
    /** Stated by the organisers. */
    CONFIRMED,

    /** Taken from a past Edition's record rather than confirmed fresh. */
    ARCHIVED,

    /** Reconstructed from public sources, or derived. Never presented as fact without a caveat. */
    UNVERIFIED,
}
