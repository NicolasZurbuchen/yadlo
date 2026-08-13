package io.nicolaszurbuchen.yadlo.common.content.domain.model

import kotlin.time.Instant

/**
 * One Happening at one time on one FestivalDay — the atomic unit: what gets favourited, what a
 * reminder fires for.
 *
 * [happening] and [day] are resolved rather than the ids the JSON has to use, so a Programme row
 * does not repeat the same join. [day] is authored, never inferred from [start], which is what
 * keeps a 01:30 set on Friday.
 *
 * [id] is Edition-qualified (`2026:dubside-sat`) so a reused id cannot resurrect last year's plan.
 * [start] and [end] are never null: an all-day Happening carries its day's opening hours instead,
 * and the content validator rejects a null, so no screen has to format an absent time.
 */
data class Slot(
    val id: String,
    val happening: Happening,
    val day: FestivalDay,
    val start: Instant,
    val end: Instant,
    val provenance: Provenance,
)
