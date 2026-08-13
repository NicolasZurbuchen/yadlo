package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class VenueDto(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val provenance: String,
)
