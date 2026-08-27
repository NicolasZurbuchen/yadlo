package io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FigureDto(
    val id: String,
    val value: String,
    val label: String,
    val provenance: String,
)
