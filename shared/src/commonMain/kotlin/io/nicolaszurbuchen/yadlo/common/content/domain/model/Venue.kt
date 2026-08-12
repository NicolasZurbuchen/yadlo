package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * Where an Edition takes place. One per Edition, and part of the frozen record — if the festival
 * ever moves, the 2026 archive must still say Preverenges.
 */
data class Venue(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val provenance: Provenance,
)
