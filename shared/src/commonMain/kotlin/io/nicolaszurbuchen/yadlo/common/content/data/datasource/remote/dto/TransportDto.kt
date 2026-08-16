package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

/** `transports`. Every mode carries `departures`, and it is null on all of them but the night bus. */
@Serializable
data class TransportDto(
    val modes: List<ModeDto> = emptyList(),
    val provenance: String,
) {
    @Serializable
    data class ModeDto(
        val id: String,
        val name: String,
        val body: String? = null,
        val facts: List<FactDto> = emptyList(),
        val links: List<InfoLinkDto> = emptyList(),
        val departures: List<DepartureDto>? = null,
    )

    @Serializable
    data class FactDto(
        val id: String,
        val text: String,
        val caveat: Boolean,
    )

    @Serializable
    data class DepartureDto(
        val id: String,
        val night: String,
        val times: List<TimeDto> = emptyList(),
    )

    @Serializable
    data class TimeDto(
        val time: String,
        val note: String? = null,
    )
}
