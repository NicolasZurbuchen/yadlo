package io.nicolaszurbuchen.yadlo.common.content.domain.model

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
