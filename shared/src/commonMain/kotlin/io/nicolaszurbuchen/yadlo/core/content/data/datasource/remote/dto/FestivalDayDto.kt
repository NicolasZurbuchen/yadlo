package io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

/**
 * [start] and [end] stay Strings so a malformed instant is rejected by the mapper against a named
 * field rather than surfacing as an opaque deserialization failure over the whole bundle.
 */
@Serializable
data class FestivalDayDto(
    val id: String,
    val name: String,
    val date: String,
    val start: String,
    val end: String,
    val provenance: String,
)
