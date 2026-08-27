package io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PartnerDto(
    val id: String,
    val name: String,
    val url: String? = null,
    val logo: ImageDto? = null,
)
