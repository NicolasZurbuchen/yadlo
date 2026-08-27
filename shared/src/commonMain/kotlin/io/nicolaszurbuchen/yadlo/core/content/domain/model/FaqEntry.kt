package io.nicolaszurbuchen.yadlo.core.content.domain.model

/**
 * One question the festival answers in its own words.
 *
 * The FAQ exists because the plainest question a first-time visitor asks — *is entry free?* — had
 * no home in any of the four tabs, which is the whole problem in miniature: the association's
 * information is split across a stale website and a live Instagram, so ordinary questions have
 * nowhere to live. See SPEC.md § Screens.
 */
data class FaqEntry(
    val id: String,
    val question: String,
    val answer: String,
    val provenance: Provenance,
)
