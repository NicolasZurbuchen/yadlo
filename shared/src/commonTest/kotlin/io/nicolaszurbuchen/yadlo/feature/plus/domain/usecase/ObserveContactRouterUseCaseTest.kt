package io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase

import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Contact
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
    fun invoke_carriesThePostalAddress() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withContact())) }

            assertEquals(listOf("Avenue de la Plage 1", "1028 Préverenges"), routerFrom(repository)?.addressLines)
        }

    @Test
    fun invoke_theStaffAddress_isStillInTheDirectory() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withContact())) }

            // Recruiting moved to its own screen, but the address did not leave the address book:
            // someone browsing the directory for the staff desk still finds it here.
            assertEquals("staff@yadlo.ch", routerFrom(repository)?.emails?.firstOrNull { it.id == "staff" }?.address)
        }

    private suspend fun routerFrom(repository: FakeContentRepository) = ObserveContactRouterUseCase(repository)().first()

    private fun withContact() =
        festival {
            copy(
                contact =
                    Contact(
                        addressLines = listOf("Avenue de la Plage 1", "1028 Préverenges"),
                        phone = null,
                        emails =
                            listOf(
                                Contact.Email(id = "hello", address = "hello@yadlo.ch", label = "Informations", responsible = null),
                                Contact.Email(
                                    id = "musique",
                                    address = "musique@yadlo.ch",
                                    label = "Programmation musicale",
                                    responsible = "Jeremy B.",
                                ),
                                Contact.Email(id = "staff", address = "staff@yadlo.ch", label = "Staff", responsible = null),
                            ),
                        provenance = Provenance.CONFIRMED,
                    ),
            )
        }
}
