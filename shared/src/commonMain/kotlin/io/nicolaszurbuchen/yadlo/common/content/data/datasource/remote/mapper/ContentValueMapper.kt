package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.mapper

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.error.AppError
import io.nicolaszurbuchen.yadlo.common.error.AppException
import kotlin.time.Instant

private const val PROVENANCE_CONFIRMED = "confirmed"
private const val PROVENANCE_ARCHIVED = "archived"
private const val PROVENANCE_UNVERIFIED = "unverified"

/**
 * The two value conversions every content file needs. Both take the field they are reading so a
 * rejection names the offending path rather than only the offending value — `day[2026:fri].start`
 * is actionable from a log line, `2026-07-10T16:00:00` on its own is not.
 */
fun String.toProvenanceEnum(field: String): Provenance =
    when (this) {
        PROVENANCE_CONFIRMED -> Provenance.CONFIRMED
        PROVENANCE_ARCHIVED -> Provenance.ARCHIVED
        PROVENANCE_UNVERIFIED -> Provenance.UNVERIFIED
        else -> throw AppException(AppError.Content.MalformedField(field, this))
    }

/**
 * Rejects a bare local time, which the schema forbids: without an offset it would be read in the
 * device's zone rather than `Europe/Zurich`, quietly shifting every set time by however far the
 * visitor has travelled.
 */
fun String.toInstantValue(field: String): Instant =
    runCatching { Instant.parse(this) }
        .getOrElse { throw AppException(AppError.Content.MalformedField(field, this)) }
