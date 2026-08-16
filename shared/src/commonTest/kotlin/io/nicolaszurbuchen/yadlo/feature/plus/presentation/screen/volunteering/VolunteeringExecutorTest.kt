package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering

import app.cash.turbine.test
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Contact
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Involvement
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveVolunteeringOfferUseCase
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
class VolunteeringExecutorTest {
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
    fun onCreate_theBundleArrives_holdsTheOffer() =
        runTest {
            val repository = FakeContentRepository()
            val store = createStore(repository)

            repository.emitStatus(ready(festival = withInvolvement()))
            testDispatcher.scheduler.runCurrent()

            assertEquals("Hot'Staff", store.state.offer?.name)
            store.dispose()
        }

    @Test
    fun signupClicked_opensTheAssociationsOwnRecruitmentSite() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(VolunteeringIntent.SignupClicked("https://ehro.app/o/yadlo/"))
                // Their pipeline keeps receiving its applications rather than a personal inbox
                // forwarding them by hand in the busiest month of their year.
                assertEquals(VolunteeringLabel.OpenUrl("https://ehro.app/o/yadlo/"), awaitItem())
            }
            store.dispose()
        }

    @Test
    fun emailClicked_opensTheVisitorsOwnMailApp() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(VolunteeringIntent.EmailClicked("staff@yadlo.ch"))
                assertEquals(VolunteeringLabel.OpenUrl("mailto:staff@yadlo.ch"), awaitItem())
            }
            store.dispose()
        }

    private fun createStore(repository: FakeContentRepository): VolunteeringStore =
        VolunteeringStoreFactory(
            storeFactory = DefaultStoreFactory(),
            observeVolunteeringOffer = ObserveVolunteeringOfferUseCase(repository),
        ).create()

    private fun withInvolvement() =
        festival {
            copy(
                contact =
                    Contact(
                        addressLines = emptyList(),
                        phone = null,
                        emails = listOf(Contact.Email(id = "staff", address = "staff@yadlo.ch", label = "Staff")),
                        provenance = Provenance.CONFIRMED,
                    ),
                involvement =
                    Involvement(
                        volunteering =
                            Involvement.Volunteering(
                                name = "Hot'Staff",
                                body = "Six heures minimum.",
                                perks = listOf("Tote bag"),
                                signupUrl = "https://ehro.app/o/yadlo/",
                                contactEmailId = "staff",
                                provenance = Provenance.CONFIRMED,
                            ),
                        partnership = null,
                    ),
            )
        }
}
