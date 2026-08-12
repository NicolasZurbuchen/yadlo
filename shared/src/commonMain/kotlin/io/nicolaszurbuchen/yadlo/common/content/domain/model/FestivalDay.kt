package io.nicolaszurbuchen.yadlo.common.content.domain.model

import kotlin.time.Instant

/**
 * One named day of an Edition, whose [start] and [end] are the hours the site is open to the public
 * that day — Friday 16:00 to 02:00, not a calendar date.
 *
 * Friday's window runs past midnight, which is why a 01:30 set still belongs to Friday. Two things
 * follow, and both matter:
 *
 * - **A Slot may fall outside it.** The beach at Preverenges is public, so the morning yoga runs
 *   from 10:00 on days the site opens at 12:00. The window is what visitors are told, not a bounding
 *   box around the programme.
 * - **Which day a Slot belongs to is authored on the Slot**, never inferred by testing an instant
 *   against these times. That is what keeps a 01:30 set on Friday without the window having to be
 *   stretched to prove it.
 *
 * [date] is the calendar date the day is *named* after and is for display only. Never derive a Slot's
 * day by truncating its start to a date.
 */
data class FestivalDay(
    val id: String,
    val name: String,
    val date: String,
    val start: Instant,
    val end: Instant,
    val provenance: Provenance,
)
