package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners

import io.nicolaszurbuchen.yadlo.core.content.domain.model.PartnerTier
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Provenance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PartnersReducerTest {
    private val reducer = PartnersStoreFactory.ReducerImpl

    @Test
    fun tiersUpdated_beforeAnyEmission_thereIsNoListAndNoNotice() {
        assertNull(PartnersState().tiers)
        assertEquals(0, PartnersState().noWebsiteTaps)
    }

    @Test
    fun tiersUpdated_firstEmission_holdsTheTiers() {
        val result = with(reducer) { PartnersState().reduce(PartnersMessage.TiersUpdated(listOf(tier()))) }

        assertEquals(listOf("sponsors"), result.tiers?.map { it.id })
    }

    @Test
    fun noWebsiteTapped_countsUpSoASecondTapSaysItAgain() {
        val once = with(reducer) { PartnersState().reduce(PartnersMessage.NoWebsiteTapped) }
        val twice = with(reducer) { once.reduce(PartnersMessage.NoWebsiteTapped) }

        assertEquals(1, once.noWebsiteTaps)
        assertEquals(2, twice.noWebsiteTaps)
    }

    @Test
    fun tiersUpdated_aRefreshLanding_doesNotResetTheNotice() {
        val tapped = PartnersState(noWebsiteTaps = 3)

        val result = with(reducer) { tapped.reduce(PartnersMessage.TiersUpdated(listOf(tier()))) }

        // A refresh arriving between the tap and the message being shown would otherwise swallow it.
        assertEquals(3, result.noWebsiteTaps)
    }

    private fun tier() =
        PartnerTier(
            id = "sponsors",
            name = "Sponsors généraux",
            order = 1,
            members = emptyList(),
            provenance = Provenance.CONFIRMED,
        )
}
