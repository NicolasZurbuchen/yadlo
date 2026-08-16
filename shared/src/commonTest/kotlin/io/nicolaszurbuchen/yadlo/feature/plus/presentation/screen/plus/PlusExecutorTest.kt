package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObservePlusOverviewUseCase
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
class PlusExecutorTest {
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
    fun onCreate_beforeAnyBundle_hasNoOverviewRatherThanAnEmptyOne() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            assertNull(store.state.overview)
            store.dispose()
        }

    @Test
    fun onCreate_theBundleArrives_readsWhatThePublishedContentOffers() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository)

            repository.emitStatus(ready(happenings = listOf(stand("vegan-fabrik"))))
            testDispatcher.scheduler.runCurrent()

            assertEquals(1, store.state.overview?.foodStandCount)
            store.dispose()
        }

    @Test
    fun refresh_landsWhileTheTabIsOpen_followsIt() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository)
            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()
            assertEquals(0, store.state.overview?.foodStandCount)

            // The root of Plus is the app's home for everything that is not the programme, so it is
            // the screen most likely to be open when a refresh turns a section from absent into
            // published — and each row exists only because its section does.
            repository.emitStatus(ready(happenings = listOf(stand("vegan-fabrik"), stand("guliko"))))
            testDispatcher.scheduler.runCurrent()

            assertEquals(2, store.state.overview?.foodStandCount)
            store.dispose()
        }

    private fun createStore(repository: FakeContentRepository): PlusStore =
        PlusStoreFactory(
            storeFactory = DefaultStoreFactory(),
            observePlusOverview = ObservePlusOverviewUseCase(repository),
        ).create()
}
