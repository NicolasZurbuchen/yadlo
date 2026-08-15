package io.nicolaszurbuchen.yadlo.feature.happening.domain.model

import kotlin.time.Instant

/**
 * One of a Happening's Slots, narrowed to what a fiche's date row shows.
 *
 * The full [io.nicolaszurbuchen.yadlo.common.content.domain.model.Slot] carries its Happening back,
 * which on this screen is the Happening the whole page is about — every row would hold another copy
 * of the artist whose fiche it is.
 *
 * [dayName] rather than a day id: the row reads "Samedi", and resolving a name from an id is the
 * kind of join DECISIONS.md § the bundle is atomic says happens once, at the boundary.
 */
data class HappeningSlot(
    val id: String,
    val dayName: String,
    val start: Instant,
    val end: Instant,
)
