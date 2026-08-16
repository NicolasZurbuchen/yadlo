package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Accessibility
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Assistance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FaqEntry
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Payment
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Transport
import io.nicolaszurbuchen.yadlo.common.content.domain.model.TransportMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ObservePlusOverviewUseCaseTest {
    @Test
    fun invoke_aBundleWithNothingPublished_offersNothing() =
        runTest {
            val overview = overviewFrom(FakeContentRepository().apply { emitStatus(ready()) })

            // The tab still opens; it simply has no rows. Every one of them is derived from a
            // section, which is what stops it ever opening an empty screen.
            assertEquals(0, overview.foodStandCount)
            assertNull(overview.cashAccepted)
            assertFalse(overview.hasTransport)
            assertFalse(overview.hasAccessibility)
            assertFalse(overview.hasOpeningHours)
            assertFalse(overview.hasAssistance)
            assertEquals(0, overview.faqCount)
        }

    @Test
    fun invoke_countsOnlyStandsAmongTheHappenings() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(ready(happenings = listOf(stand("vegan-fabrik"), stand("guliko"))))
                }

            assertEquals(2, overviewFrom(repository).foodStandCount)
        }

    @Test
    fun invoke_theTwoHalves_areCountedApartFromEachOther() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            happenings =
                                listOf(
                                    stand("vegan-fabrik"),
                                    stand("guliko"),
                                    stand("la-fanfrelucherie", category = CREATEURS),
                                ),
                        ),
                    )
                }

            val overview = overviewFrom(repository)

            // Two rows, two counts. One number over both would tell someone six trucks are waiting
            // when two of them sell costumes.
            assertEquals(2, overview.foodStandCount)
            assertEquals(1, overview.makerStandCount)
        }

    @Test
    fun invoke_aHalfWithNothingInIt_countsZeroAndLosesItsRow() =
        runTest {
            val repository =
                FakeContentRepository().apply { emitStatus(ready(happenings = listOf(stand("vegan-fabrik")))) }

            assertEquals(0, overviewFrom(repository).makerStandCount)
        }

    @Test
    fun invoke_cashRefused_isTheFactTheRowWillCarry() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withPayment(cash = false))) }

            assertEquals(false, overviewFrom(repository).cashAccepted)
        }

    @Test
    fun invoke_cashTaken_isPublishedAndUnremarkable() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withPayment(cash = true))) }

            // True and null are different answers: one is a row that says nothing extra, the other
            // is no row at all.
            assertEquals(true, overviewFrom(repository).cashAccepted)
        }

    @Test
    fun invoke_aPaymentBlockThatNeverMentionsCash_stillOffersTheRow() =
        runTest {
            val payment =
                Payment(
                    methods = listOf(Payment.Method(id = "twint", name = "TWINT", accepted = true)),
                    notes = emptyList(),
                    links = emptyList(),
                    provenance = Provenance.CONFIRMED,
                )
            val repository =
                FakeContentRepository().apply {
                    emitStatus(ready(festival = festival { copy(payment = payment) }))
                }

            // The section exists, so the screen behind it has something to draw. Only the subtitle
            // is missing, and that is the mapper's problem rather than a reason to hide the row.
            assertNull(overviewFrom(repository).cashAccepted)
        }

    @Test
    fun invoke_aTransportBlockWithNoModes_isNotWorthARow() =
        runTest {
            val empty = Transport(modes = emptyList(), provenance = Provenance.CONFIRMED)
            val repository =
                FakeContentRepository().apply {
                    emitStatus(ready(festival = festival { copy(transport = empty) }))
                }

            // A published-but-empty section would open a screen that says nothing, which is exactly
            // what deriving the rows is meant to prevent.
            assertFalse(overviewFrom(repository).hasTransport)
        }

    @Test
    fun invoke_accessibilityWithNoItems_isStillWorthARow() =
        runTest {
            val empty =
                Accessibility(items = emptyList(), contactEmailId = "hello", provenance = Provenance.UNVERIFIED)
            val repository =
                FakeContentRepository().apply {
                    emitStatus(ready(festival = festival { copy(accessibility = empty) }))
                }

            // The one section whose emptiness is the content. Nothing is published about the site's
            // accessibility, and the screen exists to say so and hand over an address.
            assertTrue(overviewFrom(repository).hasAccessibility)
        }

    @Test
    fun invoke_theDaysArePublished_meansTheHoursScreenHasSomethingToDeduce() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(
                        ready(
                            days =
                                listOf(
                                    day(
                                        id = "2026:fri",
                                        name = "Vendredi",
                                        start = "2026-07-10T16:00:00+02:00",
                                        end = "2026-07-11T02:00:00+02:00",
                                    ),
                                ),
                        ),
                    )
                }

            assertTrue(overviewFrom(repository).hasOpeningHours)
        }

    @Test
    fun invoke_assistanceAndFaq_followWhatWasPublished() =
        runTest {
            val assistance =
                Assistance(
                    emergencyNumbers =
                        listOf(Assistance.EmergencyNumber(id = "ambulance", label = "Ambulance", number = "144")),
                    lostPropertyEmailId = "hello",
                    provenance = Provenance.UNVERIFIED,
                )
            val faq =
                listOf(
                    FaqEntry(id = "entree", question = "Payant ?", answer = "Non.", provenance = Provenance.CONFIRMED),
                )
            val repository =
                FakeContentRepository().apply {
                    emitStatus(ready(festival = festival { copy(assistance = assistance, faq = faq) }))
                }

            val overview = overviewFrom(repository)

            assertTrue(overview.hasAssistance)
            assertEquals(1, overview.faqCount)
        }

    @Test
    fun invoke_aRefreshLands_theTabFollowsIt() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready()) }
            val useCase = ObservePlusOverviewUseCase(repository)
            assertEquals(0, useCase().first().foodStandCount)

            repository.emitStatus(ready(happenings = listOf(stand("vegan-fabrik"))))

            // The root of Plus is the screen most likely to be open when a refresh arrives, and a
            // refresh is what turns a section from absent into published.
            assertEquals(1, useCase().first().foodStandCount)
        }

    private suspend fun overviewFrom(repository: FakeContentRepository) = ObservePlusOverviewUseCase(repository)().first()

    private fun withPayment(cash: Boolean) =
        festival {
            copy(
                payment =
                    Payment(
                        methods =
                            listOf(
                                Payment.Method(id = "carte", name = "Cartes", accepted = true),
                                Payment.Method(id = "especes", name = "Espèces", accepted = cash),
                            ),
                        notes = emptyList(),
                        links = emptyList(),
                        provenance = Provenance.CONFIRMED,
                    ),
            )
        }

    private fun transportWithModes() =
        Transport(
            modes = listOf(TransportMode(id = "pied", name = "À pied", body = null, links = emptyList(), departures = emptyList())),
            provenance = Provenance.CONFIRMED,
        )

    @Test
    fun invoke_transportWithAMode_offersTheRow() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(ready(festival = festival { copy(transport = transportWithModes()) }))
                }

            assertTrue(overviewFrom(repository).hasTransport)
        }
}
