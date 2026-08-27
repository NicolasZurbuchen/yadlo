package io.nicolaszurbuchen.yadlo.feature.happening.domain.model

import kotlin.time.Instant

/**
 * One of a Happening's Slots, narrowed to what a fiche's date row shows.
 *
 * The full [io.nicolaszurbuchen.yadlo.core.content.domain.model.Slot] carries its Happening back,
 * which on this screen is the Happening the whole page is about — every row would hold another copy
 * of the artist whose fiche it is.
 *
 * [dayName] rather than a day id: the row reads "Samedi", and resolving a name from an id is the
 * kind of join DECISIONS.md § the bundle is atomic says happens once, at the boundary.
 *
 * [dayStart] is the *FestivalDay's* own opening instant and never the Slot's, which is the whole
 * point of carrying it: a 01:30 set belongs to the evening before, so truncating [start] to a
 * calendar date would print tomorrow on the one row where it matters. It exists only to be written
 * out — nothing compares it.
 *
 * [planned] comes from the other repository entirely — the heart is a join, not a field — and it is
 * per Slot rather than per Happening because a three-day activity is three separate decisions.
 */
data class HappeningSlot(
    val id: String,
    val dayName: String,
    val dayStart: Instant,
    val start: Instant,
    val end: Instant,
    val planned: Boolean,
)
