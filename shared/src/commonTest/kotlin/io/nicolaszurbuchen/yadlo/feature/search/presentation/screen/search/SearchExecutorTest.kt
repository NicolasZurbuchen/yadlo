package io.nicolaszurbuchen.yadlo.feature.search.presentation.screen.search

import app.cash.turbine.test
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.feature.search.domain.model.SearchTopic
import io.nicolaszurbuchen.yadlo.feature.search.domain.usecase.MatchSearchQueryUseCase
import io.nicolaszurbuchen.yadlo.feature.search.domain.usecase.ObserveSearchIndexUseCase
import io.nicolaszurbuchen.yadlo.feature.search.domain.usecase.activity
import io.nicolaszurbuchen.yadlo.feature.search.domain.usecase.artist
import io.nicolaszurbuchen.yadlo.feature.search.domain.usecase.festival
import io.nicolaszurbuchen.yadlo.feature.search.domain.usecase.question
import io.nicolaszurbuchen.yadlo.feature.search.domain.usecase.searchable
import io.nicolaszurbuchen.yadlo.feature.search.domain.usecase.stand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SearchExecutorTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // region the index arriving

    @Test
    fun onCreate_beforeAnyBundle_hasNothingToSearch() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            assertNull(store.state.index)
            assertNull(store.state.results)
            store.dispose()
        }

    @Test
    fun onCreate_typingBeforeTheBundleArrives_keepsTheQueryAndAnswersOnceItDoes() =
        runTest {
            // The screen can be opened on a cold start, and a keystroke that vanished because the
            // content had not landed would be the app losing input it accepted.
            val repository = FakeContentRepository()
            val store = createStore(repository)
            testDispatcher.scheduler.runCurrent()

            store.accept(SearchIntent.QueryChanged("alf"))
            testDispatcher.scheduler.runCurrent()
            assertEquals("alf", store.state.query)
            assertNull(store.state.results)

            repository.emitStatus(searchable(happenings = listOf(artist("dj-alf", name = "DJ ALF"))))
            testDispatcher.scheduler.runCurrent()

            assertEquals(listOf("dj-alf"), store.state.results?.programme?.map { it.happening.id })
            store.dispose()
        }

    @Test
    fun refresh_aBundleLandsMidQuery_reRunsItRatherThanWaitingForTheNextKeystroke() =
        runTest {
            // A correction pushed on the Saturday shows up under a field somebody is already
            // looking at.
            val repository = FakeContentRepository()
            val store = createStore(repository)
            repository.emitStatus(searchable(happenings = listOf(artist("dj-alf", name = "DJ ALF"))))
            store.accept(SearchIntent.QueryChanged("dj"))
            testDispatcher.scheduler.runCurrent()
            assertEquals(1, store.state.results?.programme?.size)

            repository.emitStatus(
                searchable(happenings = listOf(artist("dj-alf", name = "DJ ALF"), artist("dj-two", name = "DJ Two"))),
            )
            testDispatcher.scheduler.runCurrent()

            assertEquals(2, store.state.results?.programme?.size)
            store.dispose()
        }

    // endregion

    // region typing

    @Test
    fun queryChanged_everyKeystroke_answersImmediately() =
        runTest {
            // No debounce, because there is nothing to debounce: a substring scan of a corpus this
            // size is work a phone does between two frames.
            val repository = FakeContentRepository()
            val store = createStore(repository)
            repository.emitStatus(
                searchable(happenings = listOf(activity("sup-yoga", name = "SUP Yoga"), artist("dj-alf", name = "DJ ALF"))),
            )
            testDispatcher.scheduler.runCurrent()

            store.accept(SearchIntent.QueryChanged("y"))
            testDispatcher.scheduler.runCurrent()
            assertEquals(listOf("sup-yoga"), store.state.results?.programme?.map { it.happening.id })

            store.accept(SearchIntent.QueryChanged("yo"))
            testDispatcher.scheduler.runCurrent()
            assertEquals(listOf("sup-yoga"), store.state.results?.programme?.map { it.happening.id })
            store.dispose()
        }

    @Test
    fun queryChanged_theFieldIsCleared_emptiesTheResultsRatherThanShowingEverything() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository)
            repository.emitStatus(searchable(happenings = listOf(artist("dj-alf", name = "DJ ALF"))))
            store.accept(SearchIntent.QueryChanged("alf"))
            testDispatcher.scheduler.runCurrent()

            store.accept(SearchIntent.QueryChanged(""))
            testDispatcher.scheduler.runCurrent()

            assertTrue(store.state.results?.isEmpty == true)
            store.dispose()
        }

    @Test
    fun queryChanged_aPracticalWord_reachesAScreenRatherThanTheProgramme() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository)
            repository.emitStatus(
                searchable(festival = festival { copy(faq = listOf(question("entree", "L'entrée est-elle payante ?"))) }),
            )
            testDispatcher.scheduler.runCurrent()

            store.accept(SearchIntent.QueryChanged("questions"))
            testDispatcher.scheduler.runCurrent()

            assertEquals(listOf(SearchTopic.FAQ), store.state.results?.topics)
            store.dispose()
        }

    // endregion

    // region opening a result

    @Test
    fun happeningClicked_landsOnTheFiche() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(SearchIntent.HappeningClicked("dj-alf"))
                assertEquals(SearchLabel.NavigateToHappening("dj-alf"), awaitItem())
            }
            store.dispose()
        }

    @Test
    fun happeningClicked_aStand_landsOnTheSameKindOfFiche() =
        runTest {
            // One destination for a Happening however it was found — the group it appeared in is a
            // way of reading the answer, not a second kind of result.
            val repository = FakeContentRepository()
            val store = createStore(repository)
            repository.emitStatus(searchable(happenings = listOf(stand("vegemania"))))
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(SearchIntent.HappeningClicked("vegemania"))
                assertEquals(SearchLabel.NavigateToHappening("vegemania"), awaitItem())
            }
            store.dispose()
        }

    @Test
    fun topicClicked_carriesTheTopicRatherThanADestination() =
        runTest {
            // The feature never learns that the Plus tab owns the screen behind it.
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(SearchIntent.TopicClicked(SearchTopic.PAYMENT))
                assertEquals(SearchLabel.NavigateToTopic(SearchTopic.PAYMENT), awaitItem())
            }
            store.dispose()
        }

    // endregion

    private fun createStore(repository: FakeContentRepository): SearchStore =
        SearchStoreFactory(
            storeFactory = DefaultStoreFactory(),
            observeSearchIndex = ObserveSearchIndexUseCase(repository),
            matchSearchQuery = MatchSearchQueryUseCase(),
        ).create()
}
