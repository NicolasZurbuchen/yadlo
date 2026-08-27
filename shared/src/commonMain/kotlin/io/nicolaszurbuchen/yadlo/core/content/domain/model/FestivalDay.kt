package io.nicolaszurbuchen.yadlo.core.content.domain.model

import io.nicolaszurbuchen.yadlo.core.time.FESTIVAL_TIME_ZONE
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/**
 * One named day of an Edition. **[start] and [end] are the hours the site is open**, not a calendar
 * date and not a bounding box around the programme: Friday runs 16:00 to 02:00, which is why a
 * 01:30 set belongs to Friday, and the morning yoga legitimately falls outside the window because
 * the beach is public.
 *
 * [date] is for display only. Never derive a Slot's day by truncating an instant — see [Slot.day].
 */
data class FestivalDay(
    val id: String,
    val name: String,
    val date: String,
    val start: Instant,
    val end: Instant,
    val provenance: Provenance,
)

/**
 * Midnight at the start of the first day, in the festival's own zone.
 *
 * Not the first day's [FestivalDay.start]: the site opens at 16:00 on the Friday, but the Friday
 * itself is the festival as far as anyone living it is concerned, and a countdown still reading
 * *demain* over breakfast on the day would be wrong.
 *
 * This and [liveEnd] sit beside the type they read rather than inside the one use case that first
 * needed them, because two things ask the same question from opposite directions:
 * `DerivePhaseUseCase` asks which side of a boundary *now* falls on, and the reminder planner asks
 * when the next boundary *will be*. Arithmetic that disagreed between the two would surface as a
 * notification arriving on the wrong side of the phase it announces, which is the kind of bug
 * nobody finds until the weekend it matters.
 *
 * Null for an Edition with no days — a real state off season, before any content has been fetched,
 * rather than a hedge.
 */
fun List<FestivalDay>.liveStart(): Instant? {
    if (isEmpty()) return null

    return minBy { it.start }
        .start
        .toLocalDateTime(FESTIVAL_TIME_ZONE)
        .date
        .atStartOfDayIn(FESTIVAL_TIME_ZONE)
}

/**
 * The morning after the last day, at 11:00.
 *
 * Measured from the last day's *start*, never its end: Friday ends at 02:00 on the Saturday, so an
 * end-based "morning after" lands a day early the moment any day crosses midnight. The hour is late
 * enough that the handover happens over breakfast rather than at 22:01 while people are still out.
 */
fun List<FestivalDay>.liveEnd(): Instant? {
    if (isEmpty()) return null

    return maxBy { it.start }
        .start
        .toLocalDateTime(FESTIVAL_TIME_ZONE)
        .date
        .plus(DatePeriod(days = 1))
        .atTime(MORNING_AFTER)
        .toInstant(FESTIVAL_TIME_ZONE)
}

private val MORNING_AFTER = LocalTime(hour = 11, minute = 0)
