package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.PlusOverview
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PlusReducerTest {
    private val reducer = PlusStoreFactory.ReducerImpl

    @Test
    fun overviewUpdated_beforeAnyEmission_thereIsNoOverviewRatherThanAnEmptyOne() {
        // Null is "the bundle has not landed", which is a spinner. A tab with no rows is a
        // different screen and would be a content problem, not a waiting one.
        assertNull(PlusState().overview)
    }

    @Test
    fun overviewUpdated_firstEmission_holdsWhatThePublishedContentOffers() {
        val result = with(reducer) { PlusState().reduce(PlusMessage.OverviewUpdated(overview(stands = 8))) }

        assertEquals(8, result.overview?.foodStandCount)
    }

    @Test
    fun overviewUpdated_aRefreshLandsWhileTheTabIsOpen_replacesWhatWasThere() {
        val state = PlusState(overview = overview(stands = 0))

        val result = with(reducer) { state.reduce(PlusMessage.OverviewUpdated(overview(stands = 8))) }

        assertEquals(8, result.overview?.foodStandCount)
    }

    private fun overview(stands: Int) =
        PlusOverview(
            foodStandCount = stands,
            makerStandCount = 0,
            cashAccepted = false,
            hasTransport = true,
            hasAccessibility = true,
            hasOpeningHours = true,
            hasAssistance = true,
            faqCount = 1,
            foundedYear = 2015,
            charterNames = listOf("FestiPlus"),
            partnerCount = 39,
            hasVolunteering = true,
            hasContact = true,
            socials = emptyList(),
            newsletterUrl = null,
            reportEmail = null,
        )
}
