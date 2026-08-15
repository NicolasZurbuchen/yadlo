package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FaqEntryDto(
    val id: String,
    val question: String,
    val answer: String,
    val provenance: String,
)
