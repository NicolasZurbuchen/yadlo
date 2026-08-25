package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

import app.cash.turbine.test
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.StandKind
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.CREATEURS
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveStandDirectoryUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ready
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.stand
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
class StandsExecutorTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onCreate_theKindFromTheDestination_reachesTheState() =
        runTest {
            val store = createStore(FakeContentRepository(), StandsKindUiModel.MAKERS)
            testDispatcher.scheduler.runCurrent()

            // Translated once at construction so neither the navigation package nor the mapper has
            // to name a domain type.
            assertEquals(StandKind.MAKERS, store.state.kind)
            assertNull(store.state.directory)
            store.dispose()
        }

    @Test
    fun onCreate_readsOnlyTheHalfTheDestinationAskedFor() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository, StandsKindUiModel.FOOD)

            repository.emitStatus(mixed())
            testDispatcher.scheduler.runCurrent()

            assertEquals(listOf("vegan-fabrik", "guliko"), store.state.directory?.stands?.map { it.id })
            store.dispose()
        }

    @Test
    fun onCreate_theOtherKind_readsTheOtherHalf() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository, StandsKindUiModel.MAKERS)

            repository.emitStatus(mixed())
            testDispatcher.scheduler.runCurrent()

            // Same store, same content, different destination — which is the whole reason the two
            // entries share one screen.
            assertEquals(listOf("la-fanfrelucherie"), store.state.directory?.stands?.map { it.id })
            store.dispose()
        }

    @Test
    fun markSelected_narrowsTheListWithoutTouchingTheDirectory() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository, StandsKindUiModel.FOOD)
            repository.emitStatus(ready(happenings = listOf(stand("vegan-fabrik", itemMarks = listOf(listOf("vegan"))))))
            testDispatcher.scheduler.runCurrent()

            store.accept(StandsIntent.MarkToggled("vegan"))
            testDispatcher.scheduler.runCurrent()

            // The filter is state, not a refetch: the directory stays whole and the mapper narrows
            // it, so clearing the chip costs nothing.
            assertEquals(setOf("vegan"), store.state.selectedMarks)
            assertEquals(1, store.state.directory?.stands?.size)
            store.dispose()
        }

    @Test
    fun markSelected_null_clearsTheFilter() =
        runTest {
            val store = createStore(FakeContentRepository(), StandsKindUiModel.FOOD)
            testDispatcher.scheduler.runCurrent()
            store.accept(StandsIntent.MarkToggled("vegan"))
            testDispatcher.scheduler.runCurrent()

            store.accept(StandsIntent.MarkToggled(null))
            testDispatcher.scheduler.runCurrent()

            assertTrue(store.state.selectedMarks.isEmpty())
            store.dispose()
        }

    @Test
    fun standClicked_opensTheFicheTheProgrammeAlsoOpens() =
        runTest {
            val store = createStore(FakeContentRepository(), StandsKindUiModel.FOOD)
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(StandsIntent.StandClicked("vegan-fabrik"))
                // The browse half ends where the recall half does. That fiche holds the menu and
                // the one heart that can put this stand on the Wishlist.
                assertEquals(StandsLabel.NavigateToHappening("vegan-fabrik"), awaitItem())
            }
            store.dispose()
        }

    @Test
    fun refresh_landsWhileTheListIsOpen_followsIt() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository, StandsKindUiModel.FOOD)
            repository.emitStatus(ready(happenings = listOf(stand("vegan-fabrik"))))
            testDispatcher.scheduler.runCurrent()

            repository.emitStatus(ready(happenings = listOf(stand("vegan-fabrik"), stand("guliko"))))
            testDispatcher.scheduler.runCurrent()

            assertEquals(2, store.state.directory?.stands?.size)
            store.dispose()
        }

    private fun createStore(
        repository: FakeContentRepository,
        kind: StandsKindUiModel,
    ): StandsStore =
        StandsStoreFactory(
            storeFactory = DefaultStoreFactory(),
            observeStandDirectory = ObserveStandDirectoryUseCase(repository),
            kind = kind,
        ).create()

    private fun mixed() =
        ready(
            happenings =
                listOf(
                    stand("vegan-fabrik"),
                    stand("la-fanfrelucherie", category = CREATEURS),
                    stand("guliko"),
                ),
        )
}
