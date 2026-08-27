package io.nicolaszurbuchen.yadlo.feature.search.domain.usecase

import io.nicolaszurbuchen.yadlo.core.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.core.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Payment
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.core.error.AppError
import io.nicolaszurbuchen.yadlo.feature.search.domain.model.SearchTopic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class ObserveSearchIndexUseCaseTest {
    @Test
    fun invoke_theBundleArrives_indexesEveryHappeningWhateverItIs() =
        runTest {
            // One corpus: the line-up, the activities and the stands are all reachable from one
            // query, whichever tab the magnifier was tapped on.
            val repository = FakeContentRepository()
            val useCase = ObserveSearchIndexUseCase(repository)
            repository.emitStatus(
                searchable(happenings = listOf(artist("dj-alf"), activity("sup-yoga"), stand("vegemania"))),
            )

            val index = useCase().first()

            assertEquals(listOf("dj-alf", "sup-yoga", "vegemania"), index.happenings.map { it.id })
        }

    @Test
    fun invoke_theBundleArrives_carriesTheQuestionsTheFestivalAnswers() =
        runTest {
            val repository = FakeContentRepository()
            val useCase = ObserveSearchIndexUseCase(repository)
            repository.emitStatus(
                searchable(festival = festival { copy(faq = listOf(question("entree", "L'entrée est-elle payante ?"))) }),
            )

            assertEquals(listOf("entree"), useCase().first().faq.map { it.id })
        }

    @Test
    fun invoke_contentIsNotReady_emitsNothingRatherThanAnEmptyIndex() =
        runTest {
            // Loading is not "the festival has nothing to find", and an index that said so would be
            // wrong for exactly as long as the first fetch takes.
            val repository = FakeContentRepository()
            val useCase = ObserveSearchIndexUseCase(repository)
            repository.emitStatus(ContentStatus.Unavailable(AppError.Network.Unavailable))

            assertNull(withTimeoutOrNull(1.seconds) { useCase().first() })
        }

    @Test
    fun invoke_aSecondBundle_replacesTheIndexRatherThanAddingToIt() =
        runTest {
            // A correction pushed on the Saturday has to be findable without relaunching the app.
            val repository = FakeContentRepository()
            val useCase = ObserveSearchIndexUseCase(repository)
            repository.emitStatus(searchable(happenings = listOf(artist("dj-alf"))))
            repository.emitStatus(searchable(happenings = listOf(artist("dubside"))))

            assertEquals(listOf("dubside"), useCase().first().happenings.map { it.id })
        }

    // region which practical screens can be reached

    @Test
    fun invoke_aSectionTheFestivalHasNotPublished_isNotOfferedAsATopic() =
        runTest {
            // The same rule the Plus row follows: a result that opens an empty page is worse than
            // no result, because the reader now believes the app has nothing to say.
            val repository = FakeContentRepository()
            val useCase = ObserveSearchIndexUseCase(repository)
            repository.emitStatus(searchable())

            assertFalse(SearchTopic.PAYMENT in useCase().first().topics)
        }

    @Test
    fun invoke_theSectionIsPublished_itsTopicBecomesReachable() =
        runTest {
            val repository = FakeContentRepository()
            val useCase = ObserveSearchIndexUseCase(repository)
            repository.emitStatus(searchable(festival = festival { copy(payment = payment()) }))

            assertTrue(SearchTopic.PAYMENT in useCase().first().topics)
        }

    @Test
    fun invoke_onlyFoodStandsExist_doesNotOfferTheMakersList() =
        runTest {
            val repository = FakeContentRepository()
            val useCase = ObserveSearchIndexUseCase(repository)
            repository.emitStatus(searchable(happenings = listOf(stand("vegemania", category = RESTAURATION))))

            val topics = useCase().first().topics

            assertTrue(SearchTopic.STANDS_FOOD in topics)
            assertFalse(SearchTopic.STANDS_MAKERS in topics)
        }

    @Test
    fun invoke_bothHalvesOfTheStandsExist_offersBoth() =
        runTest {
            val repository = FakeContentRepository()
            val useCase = ObserveSearchIndexUseCase(repository)
            repository.emitStatus(
                searchable(
                    happenings = listOf(stand("vegemania", category = RESTAURATION), stand("frip", category = CREATEURS)),
                ),
            )

            val topics = useCase().first().topics

            assertTrue(SearchTopic.STANDS_FOOD in topics)
            assertTrue(SearchTopic.STANDS_MAKERS in topics)
        }

    @Test
    fun invoke_daysArePublished_makesTheOpeningHoursReachable() =
        runTest {
            val repository = FakeContentRepository()
            val useCase = ObserveSearchIndexUseCase(repository)
            repository.emitStatus(searchable(days = listOf(day("ven"))))

            assertTrue(SearchTopic.HOURS in useCase().first().topics)
        }

    @Test
    fun invoke_aTierWithNoMembers_doesNotMakeThePartnersPageReachable() =
        runTest {
            val repository = FakeContentRepository()
            val useCase = ObserveSearchIndexUseCase(repository)
            repository.emitStatus(searchable(partners = emptyList()))

            assertFalse(SearchTopic.PARTNERS in useCase().first().topics)
        }

    @Test
    fun invoke_partnersArePublished_makesThePageReachable() =
        runTest {
            val repository = FakeContentRepository()
            val useCase = ObserveSearchIndexUseCase(repository)
            repository.emitStatus(searchable(partners = listOf(tier("commune"))))

            assertTrue(SearchTopic.PARTNERS in useCase().first().topics)
        }

    @Test
    fun invoke_theAppsOwnScreens_areAlwaysReachableWhateverTheContentSays() =
        runTest {
            // Notifications, confidentialité and à propos are the app rather than the festival, so
            // an edition that published nothing at all still finds them.
            val repository = FakeContentRepository()
            val useCase = ObserveSearchIndexUseCase(repository)
            repository.emitStatus(searchable())

            assertEquals(
                listOf(SearchTopic.NOTIFICATIONS, SearchTopic.PRIVACY, SearchTopic.ABOUT),
                useCase().first().topics,
            )
        }

    // endregion

    private fun payment() =
        Payment(
            headline = "Carte et TWINT uniquement",
            summary = null,
            methods = emptyList(),
            notes = emptyList(),
            provenance = Provenance.CONFIRMED,
        )
}
