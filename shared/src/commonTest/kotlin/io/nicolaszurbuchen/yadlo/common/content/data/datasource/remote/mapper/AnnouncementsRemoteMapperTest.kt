package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.mapper

import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.AnnouncementsDto
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.error.AppError
import io.nicolaszurbuchen.yadlo.common.error.AppException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AnnouncementsRemoteMapperTest {
    @Test
    fun toDomain_ordersNewestFirstRegardlessOfFileOrder() {
        val dto =
            AnnouncementsDto(
                schemaVersion = 1,
                announcements =
                    listOf(
                        announcementDto(id = "old", publishedAt = "2026-06-02T12:00:00+02:00"),
                        announcementDto(id = "new", publishedAt = "2026-08-01T09:00:00+02:00"),
                    ),
            )

        val result = dto.toDomain()

        assertEquals(listOf("new", "old"), result.map { it.id })
    }

    @Test
    fun toDomain_ordersByInstantNotByWallClockText() {
        // 00:30+02:00 is earlier than 23:00+00:00 the day before is not — comparing the strings
        // would order these backwards, which is what sorting on the parsed instant prevents.
        val dto =
            AnnouncementsDto(
                schemaVersion = 1,
                announcements =
                    listOf(
                        announcementDto(id = "a", publishedAt = "2026-07-11T00:30:00+02:00"),
                        announcementDto(id = "b", publishedAt = "2026-07-10T23:00:00+00:00"),
                    ),
            )

        assertEquals(listOf("b", "a"), dto.toDomain().map { it.id })
    }

    @Test
    fun toDomain_keepsANullUrlNull() {
        val dto = AnnouncementsDto(schemaVersion = 1, announcements = listOf(announcementDto(url = null)))

        assertNull(dto.toDomain().single().url)
    }

    @Test
    fun toDomain_keepsANullEditionIdNull() {
        // Null means the annonce is about the festival itself and survives every edition.
        val dto = AnnouncementsDto(schemaVersion = 1, announcements = listOf(announcementDto(editionId = null)))

        assertNull(dto.toDomain().single().editionId)
    }

    @Test
    fun toDomain_emptyFile_mapsToNoAnnouncements() {
        assertTrue(AnnouncementsDto(schemaVersion = 1).toDomain().isEmpty())
    }

    @Test
    fun toDomain_malformedPublishedAt_rejectsTheFile() {
        val dto = AnnouncementsDto(schemaVersion = 1, announcements = listOf(announcementDto(id = "x", publishedAt = "soon")))

        val exception = assertFailsWith<AppException> { dto.toDomain() }

        assertEquals(
            AppError.Content.MalformedField(field = "announcement[x].publishedAt", detail = "soon"),
            exception.error,
        )
    }

    @Test
    fun published_json_parsesAndIgnoresFieldsThisBuildDoesNotKnow() {
        val json =
            """
            {
              "schemaVersion": 1,
              "announcements": [
                {
                  "id": "aftermovie-2026",
                  "publishedAt": "2026-08-01T09:00:00+02:00",
                  "editionId": "2026",
                  "title": "Merci pour cette édition",
                  "body": "L'aftermovie est en ligne.",
                  "url": "https://www.youtube.com/watch?v=v2G5c5046t0",
                  "provenance": "unverified",
                  "pinned": true
                }
              ]
            }
            """.trimIndent()

        val announcement =
            contentJson
                .decodeFromString<AnnouncementsDto>(json)
                .toDomain()
                .single()

        assertEquals("Merci pour cette édition", announcement.title)
        assertEquals(Provenance.UNVERIFIED, announcement.provenance)
    }
}

/** Configured like the app's own client, so the wire-format test exercises the real leniency. */
private val contentJson = Json { ignoreUnknownKeys = true }

private fun announcementDto(
    id: String = "programme-2026",
    publishedAt: String = "2026-06-02T12:00:00+02:00",
    editionId: String? = "2026",
    url: String? = null,
): AnnouncementsDto.AnnouncementDto =
    AnnouncementsDto.AnnouncementDto(
        id = id,
        publishedAt = publishedAt,
        title = "Le programme complet est en ligne",
        body = "Concerts, activités nautiques, coin enfant et jeux.",
        editionId = editionId,
        url = url,
        provenance = "unverified",
    )
