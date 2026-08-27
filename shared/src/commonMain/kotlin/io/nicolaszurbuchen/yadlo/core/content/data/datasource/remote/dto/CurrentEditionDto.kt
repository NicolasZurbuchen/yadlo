package io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

/**
 * `festival.json`, read for the one field that decides which edition file to fetch next.
 *
 * The same reasoning as [SchemaVersionDto]: working out *what to do with* a document must not
 * require parsing all of it. Reading the whole [FestivalDto] here coupled the fetch order to every
 * field in the file, so a section this build could not read stopped the edition being fetched at
 * all — and did it by throwing, from a place nothing was catching.
 */
@Serializable
data class CurrentEditionDto(
    val currentEditionId: String,
)
