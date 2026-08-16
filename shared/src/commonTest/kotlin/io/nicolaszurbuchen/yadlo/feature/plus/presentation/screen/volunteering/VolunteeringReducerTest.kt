package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.VolunteeringOffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class VolunteeringReducerTest {
    private val reducer = VolunteeringStoreFactory.ReducerImpl

    @Test
    fun offerUpdated_beforeAnyEmission_hasNotLoaded() {
        assertFalse(VolunteeringState().hasLoaded)
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
                email = "staff@yadlo.ch",
            )

        val result = with(reducer) { VolunteeringState().reduce(VolunteeringMessage.OfferUpdated(offer)) }

        assertTrue(result.hasLoaded)
        assertEquals("Hot'Staff", result.offer?.name)
    }

    @Test
    fun offerUpdated_aNullOffer_isLoadedRatherThanStillWaiting() {
        // Recruiting closed is an answer. Without the flag the screen would spin forever on it.
        val result = with(reducer) { VolunteeringState().reduce(VolunteeringMessage.OfferUpdated(null)) }

        assertTrue(result.hasLoaded)
        assertNull(result.offer)
    }
}
