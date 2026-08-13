package io.nicolaszurbuchen.yadlo.common.content.domain.model

import kotlin.time.Instant

/**
 * A dated message from the association — the *Annonce* of CONTEXT.md. The only block that appears
 * in all five Phases, and the reason to open the app on the 361 days when nothing is happening.
 */
data class Announcement(
    val id: String,
    val publishedAt: Instant,
    val title: String,
    val body: String?,
    /** Null when the annonce is about the festival itself rather than one year, and so survives it. */
    val editionId: String?,
    /**
     * A plain link, never a typed internal action: null simply means the card is not tappable.
     * Content outlives app versions, and a dead deep link into a renamed screen is a worse failure
     * than an ordinary broken web link.
     */
    val url: String?,
    val provenance: Provenance,
)
