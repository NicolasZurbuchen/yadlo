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
 */
data class PlannedDay(
    val id: String,
    val name: String,
    val start: Instant,
    val slots: List<PlannedSlot>,
)
