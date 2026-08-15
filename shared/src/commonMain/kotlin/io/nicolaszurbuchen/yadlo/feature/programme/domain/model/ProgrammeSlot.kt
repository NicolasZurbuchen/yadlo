package io.nicolaszurbuchen.yadlo.feature.programme.domain.model

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Price
import kotlin.time.Instant

/**
 * One Slot narrowed to what a Programme row shows.
 *
 * A row needs a name, a Category, a time and a price, and nothing else — not the biography, not the
 * genres, not the images, not the booking URL. Narrowing here rather than handing the presentation
 * layer a whole [io.nicolaszurbuchen.yadlo.common.content.domain.model.Slot] is what keeps the
 * variant test — an Activity has a price, an Artist does not — in the layer allowed to ask it.
 *
 * [happeningId] rather than the Happening, because the only thing the row does with it is open the
 * fiche. [price] is null for an Artist and for anything else the festival does not charge for
 * separately; `free` inside it is a different statement, and the row writes the two differently.
 */
data class ProgrammeSlot(
    val id: String,
    val dayId: String,
    val happeningId: String,
    val name: String,
    val categoryId: String,
    val categoryName: String,
    val start: Instant,
    val end: Instant,
    val price: Price?,
)
