package io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.model

import kotlin.time.Instant

/**
 * One Slot the visitor put on their Plan, narrowed to what the timeline row shows.
 *
 * The same shape as the Programme's row and for the same reasons — DECISIONS.md § Mon Yadlo layout
 * asks for "the same row vocabulary as Programme", and a saved set should not read differently from
 * the list it was saved off.
 *
 * [happeningId] rather than the Happening, because the only thing the row does with it is open the
 * fiche — which is also the only place this Slot can be taken off the Plan again.
 */
data class PlannedSlot(
    val id: String,
    val happeningId: String,
    val name: String,
    val categoryId: String,
    val categoryName: String,
    val start: Instant,
    val end: Instant,
)
