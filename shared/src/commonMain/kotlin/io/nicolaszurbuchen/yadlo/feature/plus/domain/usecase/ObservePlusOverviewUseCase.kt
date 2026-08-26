package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.model.StandKind
import io.nicolaszurbuchen.yadlo.common.content.domain.model.StandingLink
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.PlusOverview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/**
 * **These two stay raw, unlike the newsletter that used to sit beside them.** Both name one entry
 * of a published list, and the difference is what happens to the rest of that list: nothing
 * downstream wants a standing link the app cannot act on, so `StandingLink` resolves it at the
 * edge and the string never travels. Every payment method and every address does travel — the
 * Paiement screen draws all four, the Contact screen all nine — so the list has to cross the
 * boundary whole and the match has to happen here, wherever the id is named.
 *
 * Typing them would move the string rather than remove it, which is the objection #61 raised
 * against hoisting the newsletter to a shared constant.
 *
 * Cash refused is a fact worth writing on the row, and the method's own id is how the content says
 * it.
 */
private const val CASH_METHOD_ID = "especes"

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
                    // Every FestivalDay carries its own opening window, so the Horaires screen has
                    // something to draw the moment the programme does.
                    hasOpeningHours = status.bundle.edition.days.isNotEmpty(),
                    hasAssistance = festival.assistance?.emergencyNumbers.orEmpty().isNotEmpty(),
                    faqCount = festival.faq.size,
                    foundedYear = festival.story?.foundedYear,
                    charterNames = festival.charters.map { it.name },
                    partnerCount = status.bundle.edition.partners.sumOf { it.members.size },
                    hasVolunteering = festival.involvement?.volunteering != null,
                    hasContact = festival.contact?.emails.orEmpty().isNotEmpty(),
                    socials = festival.social,
                    newsletterUrl = festival.links[StandingLink.NEWSLETTER],
                    reportEmail =
                        festival.contact?.emails?.firstOrNull { it.id == GENERAL_EMAIL_ID }?.address,
                )
            }
}
