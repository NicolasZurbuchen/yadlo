package io.nicolaszurbuchen.yadlo.feature.home.domain.model

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Announcement
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Figure

/**
 * The ContentBundle narrowed to what Accueil reads, and nothing more — the bundle itself never
 * reaches a screen.
 *
 * [days] is here even though Accueil never lists a day: the Phase is derived from it, and the
 * countdown targets the first one. [hasPublishedProgramme] travels with it for the same reason —
 * it is the other half of what decides a Phase, and reading it here keeps the slot list out of a
 * screen that has no use for sixty Slots.
 */
data class HomeContent(
    val editionName: String,
    val days: List<FestivalDay>,
    val hasPublishedProgramme: Boolean,
    val announcements: List<Announcement>,
    val figures: List<Figure>,
)
