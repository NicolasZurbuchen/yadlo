package io.nicolaszurbuchen.yadlo.feature.search.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Festival
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.model.StandKind
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.feature.search.domain.model.SearchIndex
import io.nicolaszurbuchen.yadlo.feature.search.domain.model.SearchTopic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/**
 * Everything the current Edition can answer, kept in step with the bundle.
 *
 * **One corpus, whichever door the search was opened from.** The magnifier in the toolbar is the
 * same on every tab and this is why it can be: there is no per-screen index to pick between, so a
 * query typed from the Programme reaches the payment page and a query typed from Plus reaches the
 * line-up. The failure this avoids is the quiet one — typing `twint` into a Programme-scoped search,
 * getting nothing, and concluding the app does not know what TWINT is.
 *
 * **Observed rather than read once.** A bundle can refresh mid-festival, and a corrected FAQ answer
 * or a stand added on the Saturday should be findable without relaunching the app.
 *
 * **The current Edition only.** Archives load on demand, so covering them would mean either
 * fetching every edition at launch — which wrecks the cold start the offline story rests on — or a
 * search that silently does not cover what it appears to, which is the scoped-corpus mistake again
 * wearing a date.
 */
class ObserveSearchIndexUseCase(
    private val contentRepository: ContentRepository,
) {
    operator fun invoke(): Flow<SearchIndex> =
        contentRepository
            .observeStatus()
            .filterIsInstance<ContentStatus.Ready>()
            .map { status ->
                val festival = status.bundle.festival
                val edition = status.bundle.edition
                val stands = edition.happenings.filterIsInstance<Happening.Stand>()

                SearchIndex(
                    happenings = edition.happenings,
                    topics =
                        SearchTopic.entries.filter { topic ->
                            topic.isPublished(
                                festival = festival,
                                hasDays = edition.days.isNotEmpty(),
                                hasPartners = edition.partners.any { it.members.isNotEmpty() },
                                standCategoryIds = stands.mapTo(mutableSetOf()) { it.category.id },
                            )
                        },
                    faq = festival.faq,
                )
            }
}

/**
 * A topic is offered only when the section behind it exists — the same rule `PlusOverview` applies
 * to the row that opens the same screen, and for the same reason: a result that opens an empty page
 * is worse than no result, because the reader now believes the app has nothing to say rather than
 * that they asked the wrong question.
 *
 * The last three are app screens rather than published content, so they are always there.
 */
private fun SearchTopic.isPublished(
    festival: Festival,
    hasDays: Boolean,
    hasPartners: Boolean,
    standCategoryIds: Set<String>,
): Boolean =
    when (this) {
        SearchTopic.STANDS_FOOD -> StandKind.FOOD.categoryId in standCategoryIds
        SearchTopic.STANDS_MAKERS -> StandKind.MAKERS.categoryId in standCategoryIds
        SearchTopic.PAYMENT -> festival.payment != null
        SearchTopic.ACCESS -> festival.transport?.modes.orEmpty().isNotEmpty()
        SearchTopic.HOURS -> hasDays
        SearchTopic.ASSISTANCE -> festival.assistance?.emergencyNumbers.orEmpty().isNotEmpty()
        SearchTopic.FAQ -> festival.faq.isNotEmpty()
        SearchTopic.STORY -> festival.story != null
        SearchTopic.RESPONSIBLE -> festival.charters.isNotEmpty()
        SearchTopic.PARTNERS -> hasPartners
        SearchTopic.VOLUNTEERING -> festival.involvement?.volunteering != null
        SearchTopic.CONTACT -> festival.contact?.emails.orEmpty().isNotEmpty()
        SearchTopic.NOTIFICATIONS -> true
        SearchTopic.PRIVACY -> true
        SearchTopic.ABOUT -> true
    }
