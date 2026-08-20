package io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.model

import kotlin.time.Instant

/**
 * One FestivalDay that has something saved on it, with that day's Slots in order.
 *
 * A day with nothing on it is absent rather than empty: this screen is a recall of what someone
 * chose, and three headers with one row under them says less than one header does.
 *
 * [start] is the day's own opening instant, carried so the rail can write the date without parsing
 * one back out of a string. It is never derived from the first Slot — a 01:30 set belongs to the day
 * before, and the day is authored precisely so nothing has to guess that.
 *
 * [windowStart] and [windowEnd] are the day's shape: its opening hours widened to cover anything
 * programmed outside them, which is the Programme's own axis rule and is measured the same way here
 * — over **every** Slot the edition puts on this day, not over the ones that were saved. A Plan that
 * scaled its bars to what you happened to keep would place the same Slot at a different point on the
 * two screens, and the axis is the one thing the two have to agree on.
 */
data class PlannedDay(
    val id: String,
    val name: String,
    val start: Instant,
    val windowStart: Instant,
    val windowEnd: Instant,
    val slots: List<PlannedSlot>,
)
