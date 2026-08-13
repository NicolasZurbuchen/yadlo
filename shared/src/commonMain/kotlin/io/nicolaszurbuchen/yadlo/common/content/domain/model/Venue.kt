package io.nicolaszurbuchen.yadlo.common.content.domain.model

/** Part of the frozen record: if the festival moves, the 2026 archive still says Préverenges. */
data class Venue(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val provenance: Provenance,
)
