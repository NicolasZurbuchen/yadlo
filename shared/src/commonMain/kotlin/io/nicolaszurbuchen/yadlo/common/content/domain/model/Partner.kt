package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * One sponsor or supporter of an Edition.
 *
 * [logo] is a separate concept from a Happening's [Image] on purpose. A photo gets cropped into a
 * collapsing toolbar behind a scrim; a logo must never be cropped, tinted or bled to an edge.
 * Sharing a field name is what leads to a sponsor's logo being rendered like a press shot.
 */
data class Partner(
    val id: String,
    val name: String,
    val url: String?,
    val logo: Image?,
)
