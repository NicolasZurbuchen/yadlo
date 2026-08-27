package io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.mapper

import io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto.AnnouncementsDto
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Announcement

/**
 * Newest first, sorted here rather than trusted from the file: the order annonces are appended in
 * is the order someone typed them, and Accueil's top card must be the most recent one.
 */
fun AnnouncementsDto.toDomain(): List<Announcement> =
    announcements
        .map { it.toDomain() }
        .sortedByDescending { it.publishedAt }

private fun AnnouncementsDto.AnnouncementDto.toDomain(): Announcement =
    Announcement(
        id = id,
        publishedAt = publishedAt.toInstantValue("announcement[$id].publishedAt"),
        title = title,
        body = body,
        editionId = editionId,
        url = url,
        provenance = provenance.toProvenanceEnum("announcement[$id].provenance"),
    )
