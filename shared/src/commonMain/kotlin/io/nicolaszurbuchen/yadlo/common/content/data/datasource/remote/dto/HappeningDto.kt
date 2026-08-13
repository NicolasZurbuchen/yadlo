package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

/**
 * Flat, with three optional payloads, because that is the shape the content has: [kind] names which
 * one is filled. Reading it as a sealed hierarchy would move the choice into the deserializer, where
 * an unrecognised kind becomes a parse failure over the whole bundle instead of one named happening.
 */
@Serializable
data class HappeningDto(
    val id: String,
    val kind: String,
    val name: String,
    val category: String,
    val description: String? = null,
    val images: List<ImageDto> = emptyList(),
    val provenance: String,
    val artist: ArtistDto? = null,
    val activity: ActivityDto? = null,
    val stand: StandDto? = null,
) {
    @Serializable
    data class ArtistDto(
        val genres: List<String> = emptyList(),
        val links: List<LinkDto> = emptyList(),
    )

    @Serializable
    data class ActivityDto(
        val genres: List<String> = emptyList(),
        val price: PriceDto? = null,
        val bookingRequired: Boolean = false,
        val bookingUrl: String? = null,
        val equipmentProvided: Boolean? = null,
        val suitability: String? = null,
        val supervised: Boolean? = null,
    )

    @Serializable
    data class StandDto(
        val offering: String? = null,
        val marks: List<String> = emptyList(),
        val links: List<LinkDto> = emptyList(),
        val menu: List<MenuGroupDto> = emptyList(),
    )
}
