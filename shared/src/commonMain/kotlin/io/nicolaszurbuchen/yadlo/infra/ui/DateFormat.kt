package io.nicolaszurbuchen.yadlo.infra.ui

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.month_april
import yadlo.shared.generated.resources.month_august
import yadlo.shared.generated.resources.month_december
import yadlo.shared.generated.resources.month_february
import yadlo.shared.generated.resources.month_january
import yadlo.shared.generated.resources.month_july
import yadlo.shared.generated.resources.month_june
import yadlo.shared.generated.resources.month_march
import yadlo.shared.generated.resources.month_may
import yadlo.shared.generated.resources.month_november
import yadlo.shared.generated.resources.month_october
import yadlo.shared.generated.resources.month_september
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

/**
 * `11` — the day alone, unpadded.
 *
 * For Mon Yadlo's rail, where it is set at statistic size with the weekday above it and the month
 * below, so nothing around it needs a separator to be told apart from it. Unpadded because a leading
 * zero is noise at 36sp, and there is no column of these to keep aligned — one per day block.
 */
fun Instant.formatAsDayOfMonth(zone: TimeZone): String = toLocalDateTime(zone).day.toString()

/**
 * `juillet` — the month written out, as a [UiText] rather than a string.
 *
 * The one date part in the app that cannot be formatted, only translated, which is why every other
 * date here is numeric. It is a resource lookup rather than a formatter for exactly that reason: a
 * second language becomes a second values file instead of a code change.
 */
fun Instant.monthName(zone: TimeZone): UiText = UiText.Resource(MONTH_NAMES[toLocalDateTime(zone).month.number - 1])

private val MONTH_NAMES =
    listOf(
        Res.string.month_january,
        Res.string.month_february,
        Res.string.month_march,
        Res.string.month_april,
        Res.string.month_may,
        Res.string.month_june,
        Res.string.month_july,
        Res.string.month_august,
        Res.string.month_september,
        Res.string.month_october,
        Res.string.month_november,
        Res.string.month_december,
    )

/**
 * `16:00` — 24-hour, zero-padded on both halves.
 *
 * Padded rather than `9:30` because these sit in a column of times on the Programme and in Mon
 * Yadlo, and the display face is set with tabular figures precisely so that column does not jitter;
 * a missing leading zero undoes that on its own.
 *
 * The zone is a parameter for the same reason as [formatAsShortDate]: a 01:30 set reads 01:30 for
 * everyone on the beach, whatever their phone thinks the zone is.
 */
fun Instant.formatAsTimeOfDay(zone: TimeZone): String {
    val time = toLocalDateTime(zone).time
    val hour = time.hour.toString().padStart(2, '0')
    val minute = time.minute.toString().padStart(2, '0')

    return "$hour:$minute"
}
