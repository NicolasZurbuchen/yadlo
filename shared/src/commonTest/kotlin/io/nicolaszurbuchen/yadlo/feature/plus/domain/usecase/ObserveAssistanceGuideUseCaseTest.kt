package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Assistance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Contact
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ObserveAssistanceGuideUseCaseTest {
    @Test
    fun invoke_noSectionPublished_isNull() =
        runTest {
            assertNull(guideFrom(FakeContentRepository().apply { emitStatus(ready()) }))
        }

    @Test
    fun invoke_numbersKeepTheOrderTheContentDeclares() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withAssistance())) }

            // 112 ahead of the Swiss three is the association's choice about its own visitors, and
            // not one to re-sort here.
            assertEquals(listOf("112", "144", "117"), guideFrom(repository)?.numbers?.map { it.number })
        }

    @Test
    fun invoke_aNumbersLabel_isCarriedBecauseTheDigitsAloneSayNothing() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withAssistance())) }

            assertEquals("Ambulance", guideFrom(repository)?.numbers?.get(1)?.label)
        }

    @Test
    fun invoke_theLostPropertyId_isResolvedToAnAddress() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withAssistance())) }

            // The id is a join the content asked for; the screen has no business knowing it exists.
            assertEquals("hello@yadlo.ch", guideFrom(repository)?.lostPropertyEmail)
        }

    @Test
    fun invoke_anEmailIdThatResolvesToNothing_leavesTheNumbersStanding() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(ready(festival = withAssistance(lostPropertyEmailId = "personne")))
                }

            val guide = guideFrom(repository)

            // The numbers are the half of this screen that needs nobody's confirmation, so a broken
            // reference somewhere else must not take them down.
            assertEquals(3, guide?.numbers?.size)
            assertNull(guide?.lostPropertyEmail)
        }

    private suspend fun guideFrom(repository: FakeContentRepository) = ObserveAssistanceGuideUseCase(repository)().first()

    private fun withAssistance(lostPropertyEmailId: String = "hello") =
        festival {
            copy(
                assistance =
                    Assistance(
                        emergencyNumbers =
                            listOf(
                                Assistance.EmergencyNumber(id = "urgences", label = "Urgences", number = "112"),
                                Assistance.EmergencyNumber(id = "ambulance", label = "Ambulance", number = "144"),
                                Assistance.EmergencyNumber(id = "police", label = "Police", number = "117"),
                            ),
                        lostPropertyEmailId = lostPropertyEmailId,
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
