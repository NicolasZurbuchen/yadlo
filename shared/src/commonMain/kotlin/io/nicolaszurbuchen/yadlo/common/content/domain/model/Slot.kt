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
 * **[happening] and [day] are resolved, not ids.** The content authors them as `happeningId` and
 * `dayId` because JSON has no other way to express a reference, but a Slot that only knows two
 * strings pushes the same join onto every screen that renders one — and a Programme row needs the
 * Happening's name, its Category's label and the day it belongs to before it can draw a single line.
 * The mapper does that join once, against the bundle it already holds.
 *
 * [day] is authored on the Slot, never inferred by testing [start] against a day's window. That is
 * what keeps a 01:30 set on Friday. See [FestivalDay].
 */
data class Slot(
    val id: String,
    val happening: Happening,
    val day: FestivalDay,
    val start: Instant,
    val end: Instant,
    val provenance: Provenance,
)
