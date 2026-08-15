package io.nicolaszurbuchen.yadlo.feature.programme.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentBundle
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Slot
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.feature.programme.domain.model.ProgrammeContent
import io.nicolaszurbuchen.yadlo.feature.programme.domain.model.ProgrammeSlot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/**
 * The Programme's slice of the content bundle.
 *
 * As on Accueil, only [ContentStatus.Ready] is mapped: the tab shell is not composed until the
 * bundle is ready, so no screen inside it renders a loading or an unavailable one.
 */
class ObserveProgrammeContentUseCase(
    private val repository: ContentRepository,
) {
    operator fun invoke(): Flow<ProgrammeContent> =
        repository
            .observeStatus()
            .filterIsInstance<ContentStatus.Ready>()
            .map { it.bundle.toProgrammeContent() }

    private fun ContentBundle.toProgrammeContent(): ProgrammeContent {
        // A Stand's Slots are opening windows, not appointments (CONTEXT.md § Stand). The bar being
        // open from 12:00 to 02:00 is not a fourteen-hour entry on the day's list, and DECISIONS.md
        // § What lands on the timeline draws the same line for Mon Yadlo.
        val programmed =
            edition.slots
                .filterNot { it.happening is Happening.Stand }
                // Time, then the shorter of two things starting together: an hour-long set reads
                // before the seven-hour activity it starts alongside, which is the order someone
                // scanning for "what is on at four" is reading in.
                .sortedWith(compareBy({ it.start }, { it.end }))

        val programmedCategoryIds = programmed.mapTo(mutableSetOf()) { it.happening.category.id }

        return ProgrammeContent(
            days = edition.days.sortedBy { it.start },
            // Only the Categories the list can actually produce. `restauration` and `createurs`
            // belong to Stands alone, so their chips would empty the list every time — which reads
            // as a broken filter rather than as an honest empty day.
            categories = edition.categories.filter { it.id in programmedCategoryIds }.sortedBy { it.order },
            slots = programmed.map { it.toProgrammeSlot() },
        )
    }

    private fun Slot.toProgrammeSlot(): ProgrammeSlot =
        ProgrammeSlot(
            id = id,
            dayId = day.id,
            happeningId = happening.id,
            name = happening.name,
            categoryId = happening.category.id,
            categoryName = happening.category.name,
            start = start,
            end = end,
            // Only an Activity carries one. An Artist is covered by getting in, and a row that said
            // "gratuit" under every concert would be answering a question nobody asked.
            price = (happening as? Happening.Activity)?.price,
        )
}
