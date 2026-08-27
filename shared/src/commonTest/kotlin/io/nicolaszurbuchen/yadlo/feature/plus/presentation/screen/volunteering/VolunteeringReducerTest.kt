package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering

import io.nicolaszurbuchen.yadlo.core.content.domain.model.Contact
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.VolunteeringOffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class VolunteeringReducerTest {
    private val reducer = VolunteeringStoreFactory.ReducerImpl

    @Test
    fun offerUpdated_beforeAnyEmission_carriesNoOffer() {
        assertNull(VolunteeringState().offer)
    }

    @Test
    fun offerUpdated_firstEmission_holdsTheOffer() {
        val offer =
            VolunteeringOffer(
                name = "Hot'Staff",
                body = "Six heures minimum.",
                perks = listOf("Tote bag"),
                signupUrl = "https://ehro.app/o/yadlo/",
                email = Contact.Email(id = "staff", address = "staff@yadlo.ch", label = "Staff", responsible = null),
            )

        val result = with(reducer) { VolunteeringState().reduce(VolunteeringMessage.OfferUpdated(offer)) }

        assertEquals("Hot'Staff", result.offer?.name)
    }
}
