package io.nicolaszurbuchen.yadlo.core.content.domain.model

/**
 * The three content files as one resolved snapshot, parsed once and held in memory. UseCases narrow
 * this for their screens; no screen ever receives the bundle itself.
 *
 * [announcements] may be empty while the others are present — an annonce feed that failed to parse
 * is a missing block on Accueil, not a festival the app cannot show.
 */
data class ContentBundle(
    val festival: Festival,
    val edition: Edition,
    val announcements: List<Announcement>,
)
