package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance

import app.cash.turbine.test
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Assistance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Contact
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveAssistanceGuideUseCase
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

@OptIn(ExperimentalCoroutinesApi::class)
class AssistanceExecutorTest {
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
    fun onCreate_theBundleArrives_holdsTheNumbers() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository)

            repository.emitStatus(ready(festival = withAssistance()))
            testDispatcher.scheduler.runCurrent()

            assertEquals(listOf("144"), store.state.guide?.numbers?.map { it.number })
            store.dispose()
        }

    @Test
    fun numberClicked_opensTheDialerWithTheNumberInIt() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(AssistanceIntent.NumberClicked("144"))
                // The platform's own dialer, not a call placed: nothing dials without a second tap,
                // which is what makes a row someone might brush past safe to have.
                assertEquals(AssistanceLabel.OpenUrl("tel:144"), awaitItem())
            }
            store.dispose()
        }

    @Test
    fun numberClicked_aNumberWrittenToBeRead_isStrippedBeforeItIsDialled() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(AssistanceIntent.NumberClicked("0800 14 14 14"))
                // Spaces are how a number is printed and never how it is dialled, so the published
                // string and the dialled one never have to be kept in step.
                assertEquals(AssistanceLabel.OpenUrl("tel:0800141414"), awaitItem())
            }
            store.dispose()
        }

    @Test
    fun numberClicked_anInternationalNumber_keepsItsPlus() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(AssistanceIntent.NumberClicked("+41 21 555 00 00"))
                assertEquals(AssistanceLabel.OpenUrl("tel:+41215550000"), awaitItem())
            }
            store.dispose()
        }

    @Test
    fun lostPropertyClicked_opensMail() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(AssistanceIntent.LostPropertyClicked("hello@yadlo.ch"))
                assertEquals(AssistanceLabel.OpenUrl("mailto:hello@yadlo.ch"), awaitItem())
            }
            store.dispose()
        }

    private fun createStore(repository: FakeContentRepository): AssistanceStore =
        AssistanceStoreFactory(
            storeFactory = DefaultStoreFactory(),
            observeAssistanceGuide = ObserveAssistanceGuideUseCase(repository),
        ).create()

    private fun withAssistance() =
        festival {
            copy(
                assistance =
                    Assistance(
                        emergencyNumbers =
                            listOf(Assistance.EmergencyNumber(id = "ambulance", label = "Ambulance", number = "144")),
                        lostPropertyEmailId = "hello",
                        provenance = Provenance.UNVERIFIED,
                    ),
                contact =
                    Contact(
                        addressLines = emptyList(),
                        phone = null,
                        emails = listOf(Contact.Email(id = "hello", address = "hello@yadlo.ch", label = "Infos", responsible = null)),
                        provenance = Provenance.CONFIRMED,
                    ),
            )
        }
}
