package io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentBundle
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.model.dietaryCoverage
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedItem
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedKind
import io.nicolaszurbuchen.yadlo.common.plan.domain.repository.PlanRepository
import io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.model.WishlistGroup
import io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.model.WishlistStand
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance

/**
 * The saved Stands, grouped by Category — *à essayer*.
 *
 * Saved Stands and nothing else, because Mon Yadlo never browses (DECISIONS.md § Mon Yadlo never
 * browses, it only recalls). Discovering a stand happens in Plus › Nourriture & boissons.
 *
 * Stands keep the order the content declares them in. Alphabetical would be a second ordering
 * decision for a list of roughly seven, and saved-first would reorder itself under the reader every
 * time they kept another one.
 */
class ObserveWishlistUseCase(
    private val contentRepository: ContentRepository,
    private val planRepository: PlanRepository,
) {
    operator fun invoke(): Flow<List<WishlistGroup>> =
        combine(
            contentRepository.observeStatus().filterIsInstance<ContentStatus.Ready>(),
            planRepository.observeSaved(),
        ) { status, saved ->
            status.bundle.toWishlistGroups(saved)
        }

    private fun ContentBundle.toWishlistGroups(saved: List<SavedItem>): List<WishlistGroup> {
        val wishlistedIds = saved.filter { it.kind == SavedKind.STAND }.mapTo(mutableSetOf()) { it.id }

        val stands =
            edition.happenings
                .filterIsInstance<Happening.Stand>()
                .filter { it.id in wishlistedIds }
                .groupBy { it.category.id }

        return edition.categories
            .sortedBy { it.order }
            .filter { stands.containsKey(it.id) }
            .map { category ->
                WishlistGroup(
                    categoryId = category.id,
                    categoryName = category.name,
                    stands =
                        stands.getValue(category.id).map { stand ->
                            WishlistStand(
                                id = stand.id,
                                name = stand.name,
                                offering = stand.offering,
                                dietary = stand.dietaryCoverage(),
                            )
                        },
                )
            }
    }
}
