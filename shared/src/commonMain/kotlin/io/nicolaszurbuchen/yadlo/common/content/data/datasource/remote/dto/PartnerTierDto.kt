package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PartnerTierDto(
    val id: String,
    val name: String,
    val order: Int,
    val provenance: String,
    val members: List<PartnerDto> = emptyList(),
)
