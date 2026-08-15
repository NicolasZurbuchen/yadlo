package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Transport
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/**
 * How to get there and how to get home, in the order the content declares — which is chronological
 * rather than alphabetical, so the page reads the way it is used before leaving the house.
 *
 * Passed through unreshaped for the reason [ObservePaymentUseCase] gives. The one thing a mode
 * needed doing to it — collapsing a null timetable and an empty one into the same empty list —
 * already happened in the mapper, so no screen has to tell the two apart.
 */
class ObserveTransportUseCase(
    private val contentRepository: ContentRepository,
) {
    operator fun invoke(): Flow<Transport?> =
        contentRepository
            .observeStatus()
            .filterIsInstance<ContentStatus.Ready>()
            .map { it.bundle.festival.transport }
}
