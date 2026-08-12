package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * One year's festival, and the frozen record of it. An Edition never changes once it is over.
 *
 * Contrast with the festival's live information — contact, transport, history — which is always the
 * current truth no matter which Edition is being viewed. The test for which of the two a field
 * belongs to is not "does it change every year" but **would a past-edition archive need its own
 * copy?** Browsing 2026 should show 2026's lineup and 2026's statistics, but today's contact address.
 *
 * `entry` and `openingNote` were deliberately removed from the content rather than modelled here:
 * no screen renders them yet, and a field nobody reads is a field nobody notices going stale. The
 * FAQ still answers whether entry is free. Both return as structured fields the day the Horaires
 * and Sur place screens exist, and the content validator rejects them until then so they cannot
 * quietly reappear.
 */
data class Edition(
    val id: String,
    val year: Int,
    val name: String,
    val venue: Venue,
    val days: List<FestivalDay>,
    val categories: List<Category>,
    val happenings: List<Happening>,
    val slots: List<Slot>,
    val partners: List<PartnerTier>,
    val figures: List<Figure>,
)
