package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners

import app.cash.turbine.test
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.nicolaszurbuchen.yadlo.core.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObservePartnerTiersUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ready
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.tier
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
class PartnersExecutorTest {
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
    fun onCreate_beforeAnyBundle_hasNoTiers() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            assertNull(store.state.tiers)
            store.dispose()
        }

    @Test
    fun onCreate_theBundleArrives_holdsTheTiers() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository)

            repository.emitStatus(ready(partners = listOf(tier("sponsors", order = 1, members = listOf("mbc")))))
            testDispatcher.scheduler.runCurrent()

            assertEquals(listOf("sponsors"), store.state.tiers?.map { it.id })
            store.dispose()
        }

    @Test
    fun partnerClicked_withASite_opensIt() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(PartnersIntent.PartnerClicked("https://totem.ch"))
                assertEquals(PartnersLabel.OpenUrl("https://totem.ch"), awaitItem())
            }
            store.dispose()
        }

    @Test
    fun partnerClicked_withoutASite_saysSoRatherThanDoingNothing() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            store.accept(PartnersIntent.PartnerClicked(null))
            testDispatcher.scheduler.runCurrent()

            // Five of the thirty-nine have no site, so silence would be the common case rather than
            // the edge one — and a tap that does nothing reads as a broken app.
            assertEquals(1, store.state.noWebsiteTaps)
            store.dispose()
        }

    @Test
    fun partnerClicked_withoutASite_publishesNothingForThePlatformToOpen() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(PartnersIntent.PartnerClicked(null))
                expectNoEvents()
            }
            store.dispose()
        }

    private fun createStore(repository: FakeContentRepository): PartnersStore =
        PartnersStoreFactory(
            storeFactory = DefaultStoreFactory(),
            observePartnerTiers = ObservePartnerTiersUseCase(repository),
        ).create()
}
