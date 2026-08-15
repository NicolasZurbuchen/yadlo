package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact

import app.cash.turbine.test
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Contact
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveContactRouterUseCase
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
class ContactExecutorTest {
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
    fun onCreate_theBundleArrives_holdsTheDirectory() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository)

            repository.emitStatus(ready(festival = withContact()))
            testDispatcher.scheduler.runCurrent()

            assertEquals(listOf("hello"), store.state.router?.emails?.map { it.id })
            store.dispose()
        }

    @Test
    fun emailClicked_opensTheVisitorsOwnMailApp() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(ContactIntent.EmailClicked("musique@yadlo.ch"))
                // Nothing is posted anywhere and nothing is stored: the app opens a mail and the
                // association's existing inbox receives it.
                assertEquals(ContactLabel.OpenUrl("mailto:musique@yadlo.ch"), awaitItem())
            }
            store.dispose()
        }

    @Test
    fun signupClicked_opensTheAssociationsOwnRecruitmentSite() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(ContactIntent.SignupClicked("https://ehro.app/o/yadlo/"))
                // Their pipeline keeps receiving its applications rather than a personal inbox
                // forwarding them by hand in the busiest month of their year.
                assertEquals(ContactLabel.OpenUrl("https://ehro.app/o/yadlo/"), awaitItem())
            }
            store.dispose()
        }

    private fun createStore(repository: FakeContentRepository): ContactStore =
        ContactStoreFactory(
            storeFactory = DefaultStoreFactory(),
            observeContactRouter = ObserveContactRouterUseCase(repository),
        ).create()

    private fun withContact() =
        festival {
            copy(
                contact =
                    Contact(
                        addressLines = emptyList(),
                        phone = null,
                        emails = listOf(Contact.Email(id = "hello", address = "hello@yadlo.ch", label = "Infos")),
                        provenance = Provenance.CONFIRMED,
                    ),
            )
        }
}
