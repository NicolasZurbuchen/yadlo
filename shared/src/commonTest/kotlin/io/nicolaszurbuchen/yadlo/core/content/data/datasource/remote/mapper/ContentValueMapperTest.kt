package io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.mapper

import io.nicolaszurbuchen.yadlo.core.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.core.error.AppError
import io.nicolaszurbuchen.yadlo.core.error.AppException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class ContentValueMapperTest {
    @Test
    fun toProvenanceEnum_mapsEveryDeclaredValue() {
        assertEquals(Provenance.CONFIRMED, "confirmed".toProvenanceEnum("f"))
        assertEquals(Provenance.ARCHIVED, "archived".toProvenanceEnum("f"))
        assertEquals(Provenance.UNVERIFIED, "unverified".toProvenanceEnum("f"))
    }

    @Test
    fun toProvenanceEnum_unknownValue_namesTheField() {
        val exception = assertFailsWith<AppException> { "probably".toProvenanceEnum("happening[x].provenance") }

        assertEquals(
            AppError.Content.MalformedField(field = "happening[x].provenance", detail = "probably"),
            exception.error,
        )
    }

    @Test
    fun toProvenanceEnum_isCaseSensitive() {
        // The content is machine-written and lowercase throughout. Accepting "Confirmed" here would
        // mean the validator and the app disagree about what a legal file looks like.
        assertFailsWith<AppException> { "Confirmed".toProvenanceEnum("f") }
    }

    @Test
    fun toInstantValue_keepsTheAuthoredOffset() {
        assertEquals(Instant.parse("2026-07-10T14:00:00Z"), "2026-07-10T16:00:00+02:00".toInstantValue("f"))
    }

    @Test
    fun toInstantValue_acceptsAWinterOffset() {
        // CET, not CEST. A hardcoded +02:00 anywhere would read this an hour out.
        assertEquals(Instant.parse("2026-01-10T15:00:00Z"), "2026-01-10T16:00:00+01:00".toInstantValue("f"))
    }

    @Test
    fun toInstantValue_bareLocalTime_namesTheField() {
        val exception = assertFailsWith<AppException> { "2026-07-10T16:00:00".toInstantValue("day[2026:fri].start") }

        assertEquals(
            AppError.Content.MalformedField(field = "day[2026:fri].start", detail = "2026-07-10T16:00:00"),
            exception.error,
        )
    }

    @Test
    fun toInstantValue_nonsense_namesTheField() {
        val exception = assertFailsWith<AppException> { "soon".toInstantValue("announcement[x].publishedAt") }

        assertEquals(
            AppError.Content.MalformedField(field = "announcement[x].publishedAt", detail = "soon"),
            exception.error,
        )
    }
}
