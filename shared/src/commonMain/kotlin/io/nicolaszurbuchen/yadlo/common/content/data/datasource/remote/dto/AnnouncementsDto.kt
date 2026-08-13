package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

/**
 * The envelope and its rows in one file. Nested rather than split, because `AnnouncementsDto` and
 * `AnnouncementDto` differ by one letter and would be confused at every call site.
 */
@Serializable
data class AnnouncementsDto(
    val schemaVersion: Int,
    val announcements: List<Announcement> = emptyList(),
) {
    @Serializable
    data class Announcement(
        val id: String,
        val publishedAt: String,
        val title: String,
        val body: String? = null,
        val editionId: String? = null,
        val url: String? = null,
        val provenance: String,
    )
}
