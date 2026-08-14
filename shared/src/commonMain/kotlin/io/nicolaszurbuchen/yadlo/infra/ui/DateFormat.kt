package io.nicolaszurbuchen.yadlo.infra.ui

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/**
 * `02.06.2026` — numeric and Swiss-ordered.
 *
 * Numeric rather than written out because a month name is the first string that would need
 * translating, and the app has no second language yet. Shared rather than reimplemented per screen
 * so two lists of dates cannot end up reading differently.
 *
 * The zone is a parameter, never [TimeZone.currentSystemDefault]: everything the festival publishes
 * is resolved in its own zone, so a phone that has not caught up still shows the same date.
 */
fun Instant.formatAsShortDate(zone: TimeZone): String {
    val date = toLocalDateTime(zone).date
    val day = date.day.toString().padStart(2, '0')
    val month = date.month.number.toString().padStart(2, '0')

    return "$day.$month.${date.year}"
}
