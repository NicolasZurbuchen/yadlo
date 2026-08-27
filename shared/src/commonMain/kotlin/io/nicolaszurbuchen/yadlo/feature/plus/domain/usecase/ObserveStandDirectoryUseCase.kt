package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.core.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.core.content.domain.model.StandKind
import io.nicolaszurbuchen.yadlo.core.content.domain.model.dietaryCoverage
import io.nicolaszurbuchen.yadlo.core.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandDirectory
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandListing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/**
 * One half of the stands — the food trucks and the bar, or the makers.
 *
 * **A stand matches a mark if any of its dishes carries it.** "Can I eat here" is one question, and
 * a truck with a single vegan bokit answers it. What the row then *says* is the difference between
 * covering everything it sells and covering part of it — see
 * [io.nicolaszurbuchen.yadlo.core.content.domain.model.dietaryCoverage] — so a stand the filter
 * matched always explains itself rather than leaving the reader to open the menu and find out.
 *
 * Stands keep the order the content lists them in, which is the same rule the Wishlist and the
 * Programme follow.
 */
class ObserveStandDirectoryUseCase(
    private val contentRepository: ContentRepository,
) {
    operator fun invoke(kind: StandKind): Flow<StandDirectory> =
        contentRepository
            .observeStatus()
            .filterIsInstance<ContentStatus.Ready>()
            .map { status ->
                val stands =
                    status.bundle.edition.happenings
                        .filterIsInstance<Happening.Stand>()
                        .filter { it.category.id == kind.categoryId }
                        .map { stand ->
                            StandListing(
                                id = stand.id,
                                name = stand.name,
                                offering = stand.offering,
                                imageUrl = stand.images.firstOrNull()?.url,
                                dietary = stand.dietaryCoverage(),
                            )
                        }

                StandDirectory(
                    stands = stands,
                    // Offered in the order they were first met walking the list, so the chips read
                    // the way the stands do rather than alphabetically against them.
                    marks = stands.flatMap { it.dietary.keys }.distinct(),
                )
            }
}
