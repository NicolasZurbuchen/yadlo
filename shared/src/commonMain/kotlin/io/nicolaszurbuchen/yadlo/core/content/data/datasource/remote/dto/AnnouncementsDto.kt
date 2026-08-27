package io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

/** The envelope and its rows in one file, since neither is meaningful without the other. */
@Serializable
data class AnnouncementsDto(
    val schemaVersion: Int,
    val announcements: List<AnnouncementDto> = emptyList(),
) {
    @Serializable
    data class AnnouncementDto(
        val id: String,
        val publishedAt: String,
        val title: String,
        val body: String? = null,
        val editionId: String? = null,
        val url: String? = null,
        val provenance: String,
    )
}
