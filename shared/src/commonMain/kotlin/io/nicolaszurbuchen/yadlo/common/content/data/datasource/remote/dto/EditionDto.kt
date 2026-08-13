package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

/**
 * One edition file, exactly as published. [schemaVersion] is carried rather than mapped: it decides
 * whether this build may read the bundle at all, which is the datasource's call, not the domain's.
 */
@Serializable
data class EditionDto(
    val schemaVersion: Int,
    val id: String,
    val year: Int,
    val name: String,
    val venue: VenueDto,
    val days: List<FestivalDayDto> = emptyList(),
    val categories: List<CategoryDto> = emptyList(),
    val happenings: List<HappeningDto> = emptyList(),
    val slots: List<SlotDto> = emptyList(),
    val partners: List<PartnerTierDto> = emptyList(),
    val figures: List<FigureDto> = emptyList(),
)
