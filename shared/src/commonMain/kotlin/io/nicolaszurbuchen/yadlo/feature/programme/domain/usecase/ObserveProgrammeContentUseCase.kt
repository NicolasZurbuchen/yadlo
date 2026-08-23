package io.nicolaszurbuchen.yadlo.feature.programme.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentBundle
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Slot
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.feature.programme.domain.model.CatalogueEntry
import io.nicolaszurbuchen.yadlo.feature.programme.domain.model.ProgrammeContent
import io.nicolaszurbuchen.yadlo.feature.programme.domain.model.ProgrammeSlot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/**
 * The Programme tab's slice of the content bundle, covering both of its views.
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

        // The same line drawn on the other axis: a Stand is browsed in Plus, with this screen's own
        // card, so putting the eight of them in the Catalogue too would be the second door onto
        // them that the app spends two other decisions refusing.
        //
        // By Category before name, because the Category is what the chips filter on and what a
        // reader scrolling a grid with no headers is grouping by anyway. Ordered by the content's
        // own `order`, so the grid reads down in the sequence the chips read across.
        val offered =
            edition.happenings
                .filterNot { it is Happening.Stand }
                .sortedWith(compareBy({ it.category.order }, { it.name }))

        // The chips filter both views, so they have to cover both. This is the Catalogue's own set
        // in practice — every programmed Slot hangs off one of these Happenings — and the union is
        // what keeps that true of an Activity published before its hours are.
        //
        // `restauration` and `createurs` belong to Stands alone and so appear in neither half:
        // their chips would empty the list every time, which reads as a broken filter rather than
        // as an honest empty day.
        val chipCategoryIds =
            programmed.mapTo(mutableSetOf()) { it.happening.category.id } +
                offered.map { it.category.id }

        return ProgrammeContent(
            days = edition.days.sortedBy { it.start },
            categories = edition.categories.filter { it.id in chipCategoryIds }.sortedBy { it.order },
            slots = programmed.map { it.toProgrammeSlot() },
            catalogue = offered.map { it.toCatalogueEntry() },
            // What ANNOUNCED actually means: a programme exists. Read off the Edition rather than
            // off `programmed`, so this agrees with Accueil and with the shell.
            hasPublishedProgramme = edition.slots.isNotEmpty(),
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

    private fun Happening.toCatalogueEntry(): CatalogueEntry =
        CatalogueEntry(
            id = id,
            name = name,
            categoryId = category.id,
            categoryName = category.name,
            description = description,
            // The first, as the fiche's head takes it, and already absolute by the time it is here.
            imageUrl = images.firstOrNull()?.url,
            genres =
                when (this) {
                    is Happening.Artist -> genres

                    is Happening.Activity -> genres

                    // Unreachable: Stands were filtered out above. Written rather than left to an
                    // else so a fourth kind of Happening has to answer this question deliberately.
                    is Happening.Stand -> emptyList()
                },
        )
}
