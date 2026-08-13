package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * One year's festival, and the frozen record of it. The test for what belongs here rather than in
 * the live-truth file is not "does it change every year" but **would a past-edition archive need
 * its own copy?**
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
