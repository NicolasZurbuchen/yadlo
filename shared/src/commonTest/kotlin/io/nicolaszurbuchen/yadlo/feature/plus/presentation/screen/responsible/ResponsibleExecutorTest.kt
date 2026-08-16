package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.responsible

import app.cash.turbine.test
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Charter
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveResponsiblePageUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.festival
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ready
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
class ResponsibleExecutorTest {
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
    fun onCreate_beforeAnyBundle_carriesNoPage() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            assertNull(store.state.page)
            store.dispose()
        }

    @Test
    fun onCreate_theBundleArrives_readsTheCharters() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository)

            repository.emitStatus(ready(festival = published()))
            testDispatcher.scheduler.runCurrent()

            assertEquals(listOf("festiplus"), store.state.page?.sections?.map { it.id })
            store.dispose()
        }

    @Test
    fun linkClicked_publishesTheUrlForThePlatformToOpen() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(ResponsibleIntent.LinkClicked("https://festiplus.ch/"))
                assertEquals(ResponsibleLabel.OpenUrl("https://festiplus.ch/"), awaitItem())
            }
            store.dispose()
        }

    private fun createStore(repository: FakeContentRepository): ResponsibleStore =
        ResponsibleStoreFactory(
            storeFactory = DefaultStoreFactory(),
            observeResponsiblePage = ObserveResponsiblePageUseCase(repository),
        ).create()

    private fun published() =
        festival {
            copy(
                charters =
                    listOf(
                        Charter(
                            id = "festiplus",
                            name = "FestiPlus",
                            body = "Une charte.",
                            url = "https://festiplus.ch/",
                            provenance = Provenance.CONFIRMED,
                        ),
                    ),
            )
        }
}
