package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.PlusOverview
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.StandKind
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/** Cash refused is a fact worth writing on the row; the method's own id is how the content says it. */
private const val CASH_METHOD_ID = "especes"

private const val NEWSLETTER_LINK_ID = "newsletter"

/**
 * The general address, deliberately, for *signaler une information*. The directory has nine and
 * several look closer — `communication@` most of all — but choosing between them would be guessing
 * at the association's internal division of labour, and a correction sent to the wrong desk inside
 * a small committee still reaches the right one.
 */
private const val GENERAL_EMAIL_ID = "hello"

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

                val stands = status.bundle.edition.happenings.filterIsInstance<Happening.Stand>()

                PlusOverview(
                    foodStandCount = stands.count { it.category.id == StandKind.FOOD.categoryId },
                    makerStandCount = stands.count { it.category.id == StandKind.MAKERS.categoryId },
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
                    foundedYear = festival.story?.foundedYear,
                    charterNames = festival.charters.map { it.name },
                    partnerCount = status.bundle.edition.partners.sumOf { it.members.size },
                    hasContact = festival.contact?.emails.orEmpty().isNotEmpty(),
                    socialCount = festival.social.size,
                    newsletterUrl = festival.links.firstOrNull { it.id == NEWSLETTER_LINK_ID }?.url,
                    reportEmail =
                        festival.contact?.emails?.firstOrNull { it.id == GENERAL_EMAIL_ID }?.address,
                )
            }
}
