package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment

import app.cash.turbine.test
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Payment
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObservePaymentUseCase
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
class PaymentExecutorTest {
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
    fun onCreate_theBundleArrives_holdsTheMethods() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository)

            repository.emitStatus(ready(festival = festival { copy(payment = payment()) }))
            testDispatcher.scheduler.runCurrent()

            assertTrue(store.state.hasLoaded)
            assertEquals(2, store.state.payment?.methods?.size)
            store.dispose()
        }

    @Test
    fun onCreate_aBundleWithNoPaymentBlock_isLoadedRatherThanStillWaiting() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository)

            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            // Reachable through a restored back stack over a publish that dropped the section, and
            // the screen has to be able to say so instead of spinning.
            assertTrue(store.state.hasLoaded)
            store.dispose()
        }

    @Test
    fun linkClicked_publishesTheUrlForThePlatformToOpen() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(PaymentIntent.LinkClicked("https://www.twint.ch/"))
                assertEquals(PaymentLabel.OpenUrl("https://www.twint.ch/"), awaitItem())
            }
            store.dispose()
        }

    private fun createStore(repository: FakeContentRepository): PaymentStore =
        PaymentStoreFactory(
            storeFactory = DefaultStoreFactory(),
            observePayment = ObservePaymentUseCase(repository),
        ).create()

    private fun payment() =
        Payment(
            methods =
                listOf(
                    Payment.Method(id = "carte", name = "Cartes", accepted = true),
                    Payment.Method(id = "especes", name = "Espèces", accepted = false),
                ),
            notes = emptyList(),
            links = emptyList(),
            provenance = Provenance.CONFIRMED,
        )
}
