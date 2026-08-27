package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access

import app.cash.turbine.test
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.nicolaszurbuchen.yadlo.core.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Transport
import io.nicolaszurbuchen.yadlo.core.content.domain.model.TransportMode
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveTransportUseCase
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AccessExecutorTest {
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
    fun onCreate_beforeAnyBundle_hasNotLoaded() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            assertFalse(store.state.hasLoaded)
            store.dispose()
        }

    @Test
    fun onCreate_theBundleArrives_holdsTheModesInOrder() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository)

            repository.emitStatus(ready(festival = festival { copy(transport = transport()) }))
            testDispatcher.scheduler.runCurrent()

            assertEquals(listOf("pied", "bus-nuit"), store.state.transport?.modes?.map { it.id })
            store.dispose()
        }

    @Test
    fun onCreate_aBundleWithNoTransportBlock_isLoadedRatherThanStillWaiting() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository)

            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            assertTrue(store.state.hasLoaded)
            store.dispose()
        }

    @Test
    fun linkClicked_publishesTheUrlForThePlatformToOpen() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(AccessIntent.LinkClicked("https://example.ch/701.pdf"))
                // A timetable PDF leaves the app entirely, so it is the platform's business rather
                // than the navigator's.
                assertEquals(AccessLabel.OpenUrl("https://example.ch/701.pdf"), awaitItem())
            }
            store.dispose()
        }

    private fun createStore(repository: FakeContentRepository): AccessStore =
        AccessStoreFactory(
            storeFactory = DefaultStoreFactory(),
            observeTransport = ObserveTransportUseCase(repository),
        ).create()

    private fun transport() =
        Transport(
            provenance = Provenance.CONFIRMED,
            modes =
                listOf(
                    TransportMode(
                        id = "pied",
                        name = "À pied",
                        body = null,
                        facts = emptyList(),
                        links = emptyList(),
                        departures = emptyList(),
                    ),
                    TransportMode(
                        id = "bus-nuit",
                        name = "Bus de nuit",
                        body = null,
                        facts = emptyList(),
                        links = emptyList(),
                        departures =
                            listOf(
                                TransportMode.Departure(
                                    id = "samedi",
                                    night = "Samedi",
                                    times = listOf(TransportMode.Departure.Time(time = "00:59", note = null)),
                                ),
                            ),
                    ),
                ),
        )
}
