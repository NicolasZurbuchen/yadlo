package io.nicolaszurbuchen.yadlo.feature.plus.domain.model

import kotlin.time.Instant

/**
 * One day of *Horaires* — when the site opens, when it closes, and what the programme does inside
 * that window.
 *
 * **Nothing here is authored.** [opensAt] and [closesAt] are the FestivalDay's own window, which
 * *is* the opening hours rather than a bounding box around them, and the two programme instants are
 * read off the day's Slots. A screen that answers the single question it exists for without adding
 * a field to the content is the whole reason this one is worth building before the association
 * publishes anything.
 *
 * [firstStartsAt] can fall **before** [opensAt] and legitimately does: the beach is public, so the
 * morning yoga and the climbing wall run from 10:00 on days the festival opens at 12:00. Rendering
 * that as an error would be correcting the festival about its own site.
 *
 * [hoursAreConfirmed] rather than a Provenance, for the reason HomeContent gives: Provenance is
 * domain vocabulary, and the screen only needs to know whether to print a caveat.
 */
data class OpeningDay(
    val id: String,
    val name: String,
    val opensAt: Instant,
    val closesAt: Instant,
    /** Null on a day with nothing programmed, which no published day is and a future one might be. */
    val firstStartsAt: Instant?,
    val lastEndsAt: Instant?,
    val hoursAreConfirmed: Boolean,
)
