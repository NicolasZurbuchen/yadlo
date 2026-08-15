package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * A commitment the festival has signed — FestiPlus, the Vaud charter on well-being and risk
 * prevention at events.
 *
 * A list rather than a single field: a second charter is the kind of thing an association adds
 * without warning, and it would otherwise arrive as a schema change instead of a content edit.
 */
data class Charter(
    val id: String,
    val name: String,
    val body: String,
    val url: String?,
    val provenance: Provenance,
)
