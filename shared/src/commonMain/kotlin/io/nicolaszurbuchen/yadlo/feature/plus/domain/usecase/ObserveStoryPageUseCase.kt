package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StoryPage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/**
 * *L'histoire de Yadlo*, with the Edition's figures under it.
 *
 * The join is the whole point: the origin is the same in every year's telling and lives in the
 * live-truth file, while the numbers belong to one edition and travel with its archive. Putting
 * them on one screen is a presentation decision the domain makes once, so the screen never has to
 * know it is reading two files.
 */
class ObserveStoryPageUseCase(
    private val contentRepository: ContentRepository,
) {
    operator fun invoke(): Flow<StoryPage?> =
        contentRepository
            .observeStatus()
            .filterIsInstance<ContentStatus.Ready>()
            .map { status ->
                status.bundle.festival.story?.let { story ->
                    StoryPage(
                        foundedYear = story.foundedYear,
                        body = story.body,
                        passageTitle = story.passage?.title,
                        passageBody = story.passage?.body,
                        figures = status.bundle.edition.figures,
                        // Provenance is domain vocabulary and the screen only needs to know whether
                        // to print a caveat — the same collapse HomeContent makes for the same list.
                        figuresAreConfirmed =
                            status.bundle.edition.figures.all { it.provenance == Provenance.CONFIRMED },
                    )
                }
            }
}
