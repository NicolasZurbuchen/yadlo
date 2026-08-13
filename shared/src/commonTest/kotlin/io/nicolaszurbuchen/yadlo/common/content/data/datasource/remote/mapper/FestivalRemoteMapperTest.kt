package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.mapper

import io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto.FestivalDto
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Festival
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FestivalRemoteMapperTest {
    @Test
    fun toDomain_carriesTheFieldsTheLoadingChainReads() {
        val dto =
            FestivalDto(
                schemaVersion = 1,
                name = "Yadlo",
                tagline = "Mouille ton corps, arrose ton esprit",
                currentEditionId = "2026",
                minSupportedAppVersion = "1.2.0",
            )

        val result = dto.toDomain()

        assertEquals(
            Festival(
                name = "Yadlo",
                tagline = "Mouille ton corps, arrose ton esprit",
                currentEditionId = "2026",
                minSupportedAppVersion = "1.2.0",
            ),
            result,
        )
    }

    @Test
    fun published_json_parsesWithOnlyTheModelledFields() {
        // festival.json also carries histoire, faq, contact, transports, paiement, accessibilite,
        // besoin and simpliquer. None is modelled yet, and the file must still read cleanly — this
        // is the same tolerance that lets the content grow ahead of the app.
        val json =
            """
            {
              "schemaVersion": 1,
              "name": "Yadlo",
              "tagline": "Mouille ton corps, arrose ton esprit",
              "currentEditionId": "2026",
              "minSupportedAppVersion": null,
              "histoire": { "foundedYear": 2015, "body": "…", "provenance": "confirmed" },
              "faq": [{ "id": "entree", "question": "…", "answer": "…", "provenance": "confirmed" }],
              "social": [{ "id": "instagram", "name": "Instagram", "url": "https://example.ch/" }]
            }
            """.trimIndent()

        val festival = contentJson.decodeFromString<FestivalDto>(json).toDomain()

        assertEquals("2026", festival.currentEditionId)
        assertNull(festival.minSupportedAppVersion)
    }
}

/** Configured like the app's own client, so the wire-format test exercises the real leniency. */
private val contentJson = Json { ignoreUnknownKeys = true }
