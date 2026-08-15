package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

import app.cash.turbine.test
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
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
    fun onCreate_beforeAnyBundle_hasNoDirectory() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            assertNull(store.state.directory)
            store.dispose()
        }

    @Test
    fun onCreate_theBundleArrives_groupsTheStandsByCategory() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository)

            repository.emitStatus(ready(happenings = listOf(stand("vegan-fabrik"), stand("guliko"))))
            testDispatcher.scheduler.runCurrent()

            assertEquals(2, store.state.directory?.groups?.single()?.stands?.size)
            store.dispose()
        }

    @Test
    fun markSelected_narrowsTheListWithoutTouchingTheDirectory() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository)
            repository.emitStatus(ready(happenings = listOf(stand("vegan-fabrik", marks = listOf("végan")))))
            testDispatcher.scheduler.runCurrent()

            store.accept(StandsIntent.MarkSelected("végan"))
            testDispatcher.scheduler.runCurrent()

            // The filter is state, not a refetch: the directory stays whole and the mapper narrows
            // it, so clearing the chip costs nothing.
            assertEquals("végan", store.state.selectedMark)
            assertEquals(1, store.state.directory?.groups?.single()?.stands?.size)
            store.dispose()
        }

    @Test
    fun markSelected_null_clearsTheFilter() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository)
            repository.emitStatus(ready(happenings = listOf(stand("vegan-fabrik"))))
            testDispatcher.scheduler.runCurrent()
            store.accept(StandsIntent.MarkSelected("végan"))
            testDispatcher.scheduler.runCurrent()

            store.accept(StandsIntent.MarkSelected(null))
            testDispatcher.scheduler.runCurrent()

            assertNull(store.state.selectedMark)
            store.dispose()
        }

    @Test
    fun standClicked_opensTheFicheTheProgrammeAlsoOpens() =
        runTest {
            val store = createStore(FakeContentRepository())
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
            val store = createStore(repository)
            repository.emitStatus(ready(happenings = listOf(stand("vegan-fabrik"))))
            testDispatcher.scheduler.runCurrent()

            repository.emitStatus(ready(happenings = listOf(stand("vegan-fabrik"), stand("guliko"))))
            testDispatcher.scheduler.runCurrent()

            assertEquals(2, store.state.directory?.groups?.single()?.stands?.size)
            store.dispose()
        }

    private fun createStore(repository: FakeContentRepository): StandsStore =
        StandsStoreFactory(
            storeFactory = DefaultStoreFactory(),
            observeStandDirectory = ObserveStandDirectoryUseCase(repository),
        ).create()
}
