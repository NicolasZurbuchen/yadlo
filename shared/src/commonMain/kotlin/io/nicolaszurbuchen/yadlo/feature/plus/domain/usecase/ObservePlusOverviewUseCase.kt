package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.PlusOverview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/** Cash refused is a fact worth writing on the row; the method's own id is how the content says it. */
private const val CASH_METHOD_ID = "especes"

/**
 * What the root of Plus can offer, given what has been published.
 *
 * The tab is the app's permanent home for everything that is not the programme, so it is also the
 * one screen most exposed to content that has not arrived. Deriving each row's existence from the
 * section behind it means a rolled-back publish costs a row rather than opening an empty screen —
 * and it is what lets the whole tab ship before the association has answered anything.
 */
class ObservePlusOverviewUseCase(
    private val contentRepository: ContentRepository,
) {
    operator fun invoke(): Flow<PlusOverview> =
        contentRepository
            .observeStatus()
            .filterIsInstance<ContentStatus.Ready>()
            .map { status ->
                val festival = status.bundle.festival

                PlusOverview(
                    standCount = status.bundle.edition.happenings.count { it is Happening.Stand },
                    // Absent when nothing has been published about payment, false when cash is
                    // refused, true when it is taken — three states the row reads differently.
                    cashAccepted =
                        festival.payment?.methods?.firstOrNull { it.id == CASH_METHOD_ID }?.accepted,
                    hasTransport = festival.transport?.modes.orEmpty().isNotEmpty(),
                    hasAccessibility = festival.accessibility != null,
                    // Every FestivalDay carries its own opening window, so the Horaires screen has
                    // something to draw the moment the programme does.
                    hasOpeningHours = status.bundle.edition.days.isNotEmpty(),
                    hasAssistance = festival.assistance?.emergencyNumbers.orEmpty().isNotEmpty(),
                    faqCount = festival.faq.size,
                )
            }
}
