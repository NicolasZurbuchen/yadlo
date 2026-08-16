package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.accessibility

import app.cash.turbine.test
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Accessibility
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Contact
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveAccessibilityGuideUseCase
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
class AccessibilityExecutorTest {
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
    fun onCreate_theSectionIsPublishedAndEmpty_stillArrivesWithItsAddress() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository)

            repository.emitStatus(ready(festival = withAccessibility()))
            testDispatcher.scheduler.runCurrent()

            // The 2026 state, and the one this screen was designed around: nothing is published, so
            // the address is the whole of what it has to offer.
            assertTrue(store.state.guide?.available?.isEmpty() == true)
            assertEquals("hello@yadlo.ch", store.state.guide?.contactEmail)
            store.dispose()
        }

    @Test
    fun contactClicked_opensAPlainMailRatherThanAPrefilledOne() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(AccessibilityIntent.ContactClicked("hello@yadlo.ch"))
                // No subject and no body: prefilling either would mean percent-encoding a French
                // accent for every mail client on two platforms, to save one tap on a screen that
                // has already said what to write about.
                assertEquals(AccessibilityLabel.OpenUrl("mailto:hello@yadlo.ch"), awaitItem())
            }
            store.dispose()
        }

    private fun createStore(repository: FakeContentRepository): AccessibilityStore =
        AccessibilityStoreFactory(
            storeFactory = DefaultStoreFactory(),
            observeAccessibilityGuide = ObserveAccessibilityGuideUseCase(repository),
        ).create()

    private fun withAccessibility() =
        festival {
            copy(
                accessibility =
                    Accessibility(items = emptyList(), contactEmailId = "hello", provenance = Provenance.UNVERIFIED),
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
