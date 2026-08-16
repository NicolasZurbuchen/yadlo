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

class ObserveVolunteeringOfferUseCaseTest {
    @Test
    fun invoke_noInvolvementBlockAtAll_isNull() =
        runTest {
            assertNull(offerFrom(FakeContentRepository().apply { emitStatus(ready()) }))
        }

    @Test
    fun invoke_recruitingClosed_isNullRatherThanAnEmptyOffer() =
        runTest {
            val repository =
                FakeContentRepository().apply { emitStatus(ready(festival = withInvolvement(recruiting = false))) }

            // Recruiting is a campaign, not a permanent fact. An edition that has closed its
            // applications loses the row rather than opening a page with nothing to sign up to.
            assertNull(offerFrom(repository))
        }

    @Test
    fun invoke_carriesTheAskBeforeTheOffer() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withInvolvement())) }

            val offer = offerFrom(repository)

            assertEquals("Hot'Staff", offer?.name)
            assertEquals("Six heures minimum.", offer?.body)
            assertEquals(listOf("Tote bag", "Repas végane"), offer?.perks)
            assertEquals("https://ehro.app/o/yadlo/", offer?.signupUrl)
        }

    @Test
    fun invoke_theContactId_isResolvedAgainstTheDirectory() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready(festival = withInvolvement())) }

            // The one thing a signup page cannot answer, and the screen has no way to look an id up.
            assertEquals("staff@yadlo.ch", offerFrom(repository)?.email)
        }

    @Test
    fun invoke_anIdThatIsNotInTheDirectory_losesTheTileRatherThanMailingNowhere() =
        runTest {
            val repository =
                FakeContentRepository().apply {
                    emitStatus(ready(festival = withInvolvement(contactEmailId = "nobody")))
                }

            val offer = offerFrom(repository)

            // A content bug. The signup link is still there, which is the part that matters.
            assertNull(offer?.email)
            assertEquals("https://ehro.app/o/yadlo/", offer?.signupUrl)
        }

    @Test
    fun invoke_aRefreshLands_theScreenFollowsIt() =
        runTest {
            val repository = FakeContentRepository().apply { emitStatus(ready()) }
            val useCase = ObserveVolunteeringOfferUseCase(repository)
            assertNull(useCase().first())

            repository.emitStatus(ready(festival = withInvolvement()))

            assertEquals("Hot'Staff", useCase().first()?.name)
        }

    private suspend fun offerFrom(repository: FakeContentRepository) = ObserveVolunteeringOfferUseCase(repository)().first()

    private fun withInvolvement(
        recruiting: Boolean = true,
        contactEmailId: String = "staff",
    ) = festival {
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
                        Involvement
                            .Volunteering(
                                name = "Hot'Staff",
                                body = "Six heures minimum.",
                                perks = listOf("Tote bag", "Repas végane"),
                                signupUrl = "https://ehro.app/o/yadlo/",
                                contactEmailId = contactEmailId,
                                provenance = Provenance.CONFIRMED,
                            ).takeIf { recruiting },
                    partnership = null,
                ),
        )
    }
}
