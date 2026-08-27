package io.nicolaszurbuchen.yadlo.core.content.domain.model

/**
 * [logo] is separate from a Happening's [Image] on purpose: a photo gets cropped behind a scrim, a
 * logo must never be cropped or tinted. A null [url] renders as a toast rather than a dead link.
 */
data class Partner(
    val id: String,
    val name: String,
    val url: String?,
    val logo: Image?,
)
