package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.PartnerTier
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/**
 * The 39 companies without which there is no festival, in their tiers.
 *
 * Sorted by the tier's declared `order` rather than left as authored, because the order *is* the
 * hierarchy here — sponsors above cygnes d'or above bronze — and it is the one list in the content
 * where getting the sequence wrong would be visible to the people who paid for the placement.
 */
class ObservePartnerTiersUseCase(
    private val contentRepository: ContentRepository,
) {
    operator fun invoke(): Flow<List<PartnerTier>> =
        contentRepository
            .observeStatus()
            .filterIsInstance<ContentStatus.Ready>()
            .map { status ->
                status.bundle.edition.partners
                    .sortedBy { it.order }
                    .filter { it.members.isNotEmpty() }
            }
}
