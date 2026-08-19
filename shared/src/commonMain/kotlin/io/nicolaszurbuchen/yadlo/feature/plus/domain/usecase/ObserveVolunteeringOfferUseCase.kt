package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.VolunteeringOffer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/**
 * *Devenir Hot'Staff.*
 *
 * Null when the edition is not recruiting. Volunteering is a campaign rather than a permanent fact
 * — an edition that has closed its applications should not be made to publish an empty offer, and
 * the row on the tab disappears with it rather than opening a page that says nothing.
 */
class ObserveVolunteeringOfferUseCase(
    private val contentRepository: ContentRepository,
) {
    operator fun invoke(): Flow<VolunteeringOffer?> =
        contentRepository
            .observeStatus()
            .filterIsInstance<ContentStatus.Ready>()
            .map { status ->
                val festival = status.bundle.festival

                festival.involvement?.volunteering?.let { volunteering ->
                    VolunteeringOffer(
                        name = volunteering.name,
                        body = volunteering.body,
                        perks = volunteering.perks,
                        signupUrl = volunteering.signupUrl,
                        email = festival.contact?.emails?.firstOrNull { it.id == volunteering.contactEmailId },
                    )
                }
            }
}
