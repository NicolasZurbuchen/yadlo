package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Contact
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Involvement
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ObserveContactRouterUseCaseTest {
    @Test
    fun invoke_noContactBlock_isNullBecauseAnAiguillageNeedsSomewhereToSend() =
        runTest {
            assertNull(routerFrom(FakeContentRepository().apply { emitStatus(ready()) }))
        }

    @Test
    fun invoke_carriesEveryPublishedAddressWithItsOwnLabel() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withContact())) }

            // All of them, in the content's order. Reducing nine to four concerns would be guessing
            // at how a committee divides its work.
            assertEquals(listOf("hello", "musique", "staff"), routerFrom(repository)?.emails?.map { it.id })
            assertEquals("Programmation musicale", routerFrom(repository)?.emails?.get(1)?.label)
        }

    @Test
    fun invoke_theVolunteeringContactId_isResolvedToAnAddress() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withContact())) }

            assertEquals("staff@yadlo.ch", routerFrom(repository)?.volunteeringEmail)
        }

    @Test
    fun invoke_noVolunteeringCampaign_stillLeavesTheDirectory() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(ready(festival = withContact(volunteering = false)))
                }

            val router = routerFrom(repository)

            // Recruiting is a campaign, the address book is a permanent fact. An edition that is not
            // recruiting should not have to publish an empty offer to keep its contacts.
            assertNull(router?.volunteering)
            assertNull(router?.volunteeringEmail)
            assertEquals(3, router?.emails?.size)
        }

    @Test
    fun invoke_carriesThePostalAddress() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withContact())) }

            assertEquals(listOf("Avenue de la Plage 1", "1028 Préverenges"), routerFrom(repository)?.addressLines)
        }

    private suspend fun routerFrom(repository: FakeContentRepository) = ObserveContactRouterUseCase(repository)().first()

    private fun withContact(volunteering: Boolean = true) =
        festival {
            copy(
                contact =
                    Contact(
                        addressLines = listOf("Avenue de la Plage 1", "1028 Préverenges"),
                        phone = null,
                        emails =
                            listOf(
                                Contact.Email(id = "hello", address = "hello@yadlo.ch", label = "Informations"),
                                Contact.Email(
                                    id = "musique",
                                    address = "musique@yadlo.ch",
                                    label = "Programmation musicale",
                                ),
                                Contact.Email(id = "staff", address = "staff@yadlo.ch", label = "Staff"),
                            ),
                        provenance = Provenance.CONFIRMED,
                    ),
                involvement =
                    Involvement(
                        volunteering =
                            Involvement
                                .Volunteering(
                                    name = "Hot'Staff",
                                    body = "Six heures minimum.",
                                    perks = listOf("Tote bag"),
                                    signupUrl = "https://ehro.app/o/yadlo/",
                                    contactEmailId = "staff",
                                    provenance = Provenance.CONFIRMED,
                                ).takeIf { volunteering },
                        partnership = null,
                    ),
            )
        }
}
