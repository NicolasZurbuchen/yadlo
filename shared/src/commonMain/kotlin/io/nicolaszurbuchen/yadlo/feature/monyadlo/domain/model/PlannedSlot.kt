package io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.model

import io.nicolaszurbuchen.yadlo.core.content.domain.model.Price
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
 *
 * [price] is null for an Artist and for anything the festival does not charge for separately, the
 * same as on a Programme row; `free` inside it is a different statement and the row writes the two
 * differently. It is here because a Plan is what you are doing today and half of that is what it
 * costs — an earlier version left it out on the grounds that the decision was made when the heart
 * was tapped, which is true of the decision and not of the coins in your pocket.
 */
data class PlannedSlot(
    val id: String,
    val happeningId: String,
    val name: String,
    val categoryId: String,
    val categoryName: String,
    val start: Instant,
    val end: Instant,
    val price: Price?,
)
