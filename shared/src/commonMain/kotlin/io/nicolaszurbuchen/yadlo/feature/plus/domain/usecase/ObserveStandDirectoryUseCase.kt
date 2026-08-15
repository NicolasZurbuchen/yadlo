package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandDirectory
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandGroup
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandListing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/**
 * *Nourriture & boissons* — every Stand, grouped by Category.
 *
 * **A stand matches a dietary mark if it carries it or any of its dishes does.** SCHEMA.md keeps the
 * two levels apart on purpose — a Stand mark means all of it is, an Item mark means that dish is —
 * and the difference is exactly what someone scanning a row of trucks is asking about. But they are
 * asking one question, "can I eat here", and both levels answer it. De l'Or Bokit carries no stand
 * mark and sells a `végé` bokit; a filter that hid it would be wrong about the only thing it was
 * asked.
 *
 * The row still shows only the Stand's own marks, so nothing claims the whole truck is vegan
 * because one dish is.
 *
 * Categories keep their declared order and Stands keep the order the content lists them in, which
 * is the same rule the Wishlist and the Programme follow.
 */
class ObserveStandDirectoryUseCase(
    private val contentRepository: ContentRepository,
) {
    operator fun invoke(): Flow<StandDirectory> =
        contentRepository
            .observeStatus()
            .filterIsInstance<ContentStatus.Ready>()
            .map { status ->
                val edition = status.bundle.edition
                val standsByCategory =
                    edition.happenings
                        .filterIsInstance<Happening.Stand>()
                        .groupBy { it.category.id }

                val groups =
                    edition.categories
                        .sortedBy { it.order }
                        .filter { standsByCategory.containsKey(it.id) }
                        .map { category ->
                            StandGroup(
                                categoryId = category.id,
                                categoryName = category.name,
                                stands =
                                    standsByCategory.getValue(category.id).map { stand ->
                                        StandListing(
                                            id = stand.id,
                                            name = stand.name,
                                            offering = stand.offering,
                                            marks = stand.marks,
                                            dietaryMatches =
                                                (
                                                    stand.marks +
                                                        stand.menu.flatMap { group ->
                                                            group.items.flatMap { it.marks }
                                                        }
                                                ).toSet(),
                                        )
                                    },
                            )
                        }

                StandDirectory(
                    groups = groups,
                    // Offered in the order they were first met walking the list, so the chips read
                    // the way the stands do rather than alphabetically against them.
                    marks =
                        groups
                            .flatMap { group -> group.stands.flatMap { it.dietaryMatches } }
                            .distinct(),
                )
            }
}
