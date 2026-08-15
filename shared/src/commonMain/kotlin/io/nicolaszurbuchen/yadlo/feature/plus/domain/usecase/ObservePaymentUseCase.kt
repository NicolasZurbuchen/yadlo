package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Payment
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/**
 * What the site takes. Passed through rather than reshaped: the published block is already exactly
 * what the screen draws, and a feature-side mirror of it would carry no information the reader of
 * either file does not already have — the same reasoning that let `HappeningDetail` hold a `Price`.
 *
 * Null means nothing has been published about payment, which is a screen that should not have been
 * reachable rather than an empty one; the row that opens it is derived from the same absence.
 */
class ObservePaymentUseCase(
    private val contentRepository: ContentRepository,
) {
    operator fun invoke(): Flow<Payment?> =
        contentRepository
            .observeStatus()
            .filterIsInstance<ContentStatus.Ready>()
            .map { it.bundle.festival.payment }
}
