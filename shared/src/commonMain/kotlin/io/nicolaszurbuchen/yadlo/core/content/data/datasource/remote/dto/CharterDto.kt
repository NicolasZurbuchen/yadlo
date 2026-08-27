package io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

/** One member of `responsable.charters`. The wrapper object carries nothing else, so it is not modelled. */
@Serializable
data class CharterDto(
    val id: String,
    val name: String,
    val body: String,
    val url: String? = null,
    val provenance: String,
)
