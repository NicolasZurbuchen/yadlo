package io.nicolaszurbuchen.yadlo.common.content.domain.model

import kotlin.time.Instant

/**
 * One Happening occurring at one time on one FestivalDay. The atomic unit of the programme: a
 * Happening with three timeslots has three Slots.
 *
 * This is what a user favourites and what a reminder fires for. Every Slot behaves the same way,
 * whether it is a two-hour concert or a seven-hour open activity — you can arrive late to either.
 *
 * **[start] and [end] are never null.** There is no all-day Slot: a Happening that runs for the whole
 * festival carries its day's opening hours, authored in the content and marked
 * [Provenance.UNVERIFIED] because those instants were derived rather than published. The content
 * validator rejects a null start or end, so the invariant is upheld before the app ever sees it.
 *
 * [id] is Edition-qualified (`2026:dubside-sat`) so a reused id cannot resurrect last year's saved
 * plan into this year's.
 *
 * [dayId] is authored, not derived. See [FestivalDay].
 */
data class Slot(
    val id: String,
    val happeningId: String,
    val dayId: String,
    val start: Instant,
    val end: Instant,
    val provenance: Provenance,
)
