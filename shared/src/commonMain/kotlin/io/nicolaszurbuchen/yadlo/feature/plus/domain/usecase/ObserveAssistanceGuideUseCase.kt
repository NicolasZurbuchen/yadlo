package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.AssistanceGuide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/**
 * *En cas de besoin* — the emergency numbers, and where a lost bag goes afterwards.
 *
 * The numbers keep the order the content declares them in, which puts the European 112 ahead of the
 * Swiss three. That is the association's choice about its own visitors and not one to re-sort here.
 */
class ObserveAssistanceGuideUseCase(
    private val contentRepository: ContentRepository,
) {
    operator fun invoke(): Flow<AssistanceGuide?> =
        contentRepository
            .observeStatus()
            .filterIsInstance<ContentStatus.Ready>()
            .map { status ->
                val festival = status.bundle.festival

                festival.assistance?.let { assistance ->
                    AssistanceGuide(
                        numbers = assistance.emergencyNumbers,
                        lostPropertyEmail =
                            festival.contact
                                ?.emails
                                ?.firstOrNull { it.id == assistance.lostPropertyEmailId }
                                ?.address,
                    )
                }
            }
}
