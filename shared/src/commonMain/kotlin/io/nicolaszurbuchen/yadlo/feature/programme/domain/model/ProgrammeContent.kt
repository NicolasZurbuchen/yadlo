package io.nicolaszurbuchen.yadlo.feature.programme.domain.model

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Category
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay

/**
 * The Programme's slice of the content bundle: the days you can switch between, the Categories the
 * filter chips can offer, and every Slot that belongs on a chronological list.
 *
 * [days] and [slots] arrive already ordered, so one sort decision covers the day chips, the list and
 * the day the screen opens on. [categories] is not the Edition's full list — see the UseCase.
 */
data class ProgrammeContent(
    val days: List<FestivalDay>,
    val categories: List<Category>,
    val slots: List<ProgrammeSlot>,
)
