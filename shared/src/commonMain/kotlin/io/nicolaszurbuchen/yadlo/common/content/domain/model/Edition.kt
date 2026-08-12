package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * One year's festival, and the frozen record of it. An Edition never changes once it is over.
 *
 * Contrast with the festival's live information — contact, transport, history — which is always the
 * current truth no matter which Edition is being viewed. The test for which of the two a field
 * belongs to is not "does it change every year" but **would a past-edition archive need its own
 * copy?** Browsing 2026 should show 2026's lineup and 2026's statistics, but today's contact address.
 *
 * [entry] is structured rather than only answered in the FAQ prose, because a past edition has to be
 * able to state what it cost even if a later one starts charging.
 */
data class Edition(
    val id: String,
    val year: Int,
    val name: String,
    val entry: Entry,
    val openingNote: String?,
    val venue: Venue,
    val days: List<FestivalDay>,
    val categories: List<Category>,
    val happenings: List<Happening>,
    val slots: List<Slot>,
    val partners: List<PartnerTier>,
    val figures: List<Figure>,
) {
    data class Entry(
        val free: Boolean,
        val provenance: Provenance,
    )
}
