package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.ContactRouter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/**
 * *Nous écrire* — the volunteering offer, the address directory, and where the association sits.
 *
 * Null when there is no contact block at all, because an aiguillage with nowhere to send anyone is
 * not a screen. The volunteering half is separately nullable: it is the one part of this that is a
 * campaign rather than a permanent fact, and an edition that is not recruiting should not be made
 * to publish an empty one.
 */
class ObserveContactRouterUseCase(
    private val contentRepository: ContentRepository,
) {
    operator fun invoke(): Flow<ContactRouter?> =
        contentRepository
            .observeStatus()
            .filterIsInstance<ContentStatus.Ready>()
            .map { status ->
                val festival = status.bundle.festival
                val volunteering = festival.involvement?.volunteering

                festival.contact?.let { contact ->
                    ContactRouter(
                        volunteering = volunteering,
                        volunteeringEmail =
                            contact.emails.firstOrNull { it.id == volunteering?.contactEmailId }?.address,
                        emails = contact.emails,
                        addressLines = contact.addressLines,
                    )
                }
            }
}
