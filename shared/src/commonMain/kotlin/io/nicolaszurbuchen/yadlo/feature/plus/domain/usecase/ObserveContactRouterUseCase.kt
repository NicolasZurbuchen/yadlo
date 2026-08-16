package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.ContactRouter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/**
 * *Nous écrire* — the address directory and where the association sits.
 *
 * Null when there is no contact block at all, because an aiguillage with nowhere to send anyone is
 * not a screen.
 */
class ObserveContactRouterUseCase(
    private val contentRepository: ContentRepository,
) {
    operator fun invoke(): Flow<ContactRouter?> =
        contentRepository
            .observeStatus()
            .filterIsInstance<ContentStatus.Ready>()
            .map { status ->
                status.bundle.festival.contact?.let { contact ->
                    ContactRouter(
                        emails = contact.emails,
                        addressLines = contact.addressLines,
                    )
                }
            }
}
