package io.nicolaszurbuchen.yadlo.feature.programme.domain.model

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Category
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay

/**
 * The Programme tab's slice of the content bundle, for both of the views it can show: the days you
 * can switch between, the Categories the filter chips can offer, every Slot that belongs on a
 * chronological list, and every Happening that belongs in the Catalogue.
 *
 * [days], [slots] and [catalogue] arrive already ordered, so one sort decision covers the day chips,
 * the list, the grid and the day the screen opens on. [categories] is not the Edition's full list —
 * see the UseCase.
 *
 * [hasPublishedProgramme] travels with the content because the screen's opening view follows the
 * Phase, and it is the half of a Phase the clock cannot supply. It is read off the Edition's own
 * Slots rather than off [slots], which has already had the Stands' opening windows filtered out of
 * it — the three places in the app that derive a Phase have to derive the same one.
 */
data class ProgrammeContent(
    val days: List<FestivalDay>,
    val categories: List<Category>,
    val slots: List<ProgrammeSlot>,
    val catalogue: List<CatalogueEntry>,
    val hasPublishedProgramme: Boolean,
)
