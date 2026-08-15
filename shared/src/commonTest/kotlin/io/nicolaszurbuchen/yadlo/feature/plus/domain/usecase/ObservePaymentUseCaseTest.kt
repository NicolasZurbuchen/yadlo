package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.InfoLink
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Payment
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ObservePaymentUseCaseTest {
    @Test
    fun invoke_noSectionPublished_isNull() =
        runTest {
            assertNull(ObservePaymentUseCase(FakeContentRepository().apply { emitStatus(ready()) })().first())
        }

    @Test
    fun invoke_carriesBothTheAcceptedAndTheRefused() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withPayment())) }

            val payment = ObservePaymentUseCase(repository)().first()

            // "Espèces, non" is the whole point of the screen. A pass-through that quietly dropped
            // the refused method would remove the one fact worth reading before leaving home.
            assertEquals(
                listOf("carte" to true, "especes" to false),
                payment?.methods?.map { it.id to it.accepted },
            )
        }

    @Test
    fun invoke_notesAndLinks_reachTheScreenUnchanged() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withPayment())) }

            val payment = ObservePaymentUseCase(repository)().first()

            assertEquals(listOf("sans-especes"), payment?.notes?.map { it.id })
            assertEquals("twint.ch", payment?.links?.single()?.label)
        }

    @Test
    fun invoke_aRefreshLands_theScreenFollowsIt() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready()) }
            val useCase = ObservePaymentUseCase(repository)
            assertNull(useCase().first())

            repository.emitStatus(ready(festival = withPayment()))

            // A payment rule corrected during the festival is exactly the case the content pipeline
            // exists for, and this screen has to follow it without a relaunch.
            assertEquals(2, useCase().first()?.methods?.size)
        }

    private fun withPayment() =
        festival {
            copy(
                payment =
                    Payment(
                        methods =
                            listOf(
                                Payment.Method(id = "carte", name = "Cartes", accepted = true),
                                Payment.Method(id = "especes", name = "Espèces", accepted = false),
                            ),
                        notes = listOf(Payment.Note(id = "sans-especes", body = "Aucun stand n'accepte les espèces.")),
                        links =
                            listOf(
                                InfoLink(
                                    id = "twint",
                                    label = "twint.ch",
                                    sublabel = "Site officiel",
                                    url = "https://www.twint.ch/",
                                ),
                            ),
                        provenance = Provenance.CONFIRMED,
                    ),
            )
        }
}
