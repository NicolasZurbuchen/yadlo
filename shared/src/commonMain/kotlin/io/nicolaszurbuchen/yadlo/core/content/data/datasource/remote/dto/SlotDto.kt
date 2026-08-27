package io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SlotDto(
    val id: String,
    val happeningId: String,
    val dayId: String,
    val start: String,
    val end: String,
    val provenance: String,
)
