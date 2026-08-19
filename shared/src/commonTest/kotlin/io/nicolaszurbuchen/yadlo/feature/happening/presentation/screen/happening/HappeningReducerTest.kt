package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening

import io.nicolaszurbuchen.yadlo.feature.happening.domain.model.HappeningDetail
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

class HappeningReducerTest {
    private val reducer = HappeningStoreFactory.ReducerImpl

    @Test
    fun detailUpdated_firstEmission_holdsTheFicheAndMarksTheScreenLoaded() {
        val state = HappeningState(now = NOW)

        val result = with(reducer) { state.reduce(HappeningMessage.DetailUpdated(detail())) }

        assertEquals("DJ ALF", result.detail?.name)
        assertTrue(result.isLoaded)
    }

    @Test
    fun detailUpdated_null_staysLoadedSoTheScreenCanSayTheFicheIsGone() {
        // "Not yet" and "no longer" are different screens, and only isLoaded separates them.
        val state = HappeningState(now = NOW)

        val result = with(reducer) { state.reduce(HappeningMessage.DetailUpdated(null)) }

        assertNull(result.detail)
        assertTrue(result.isLoaded)
    }

    @Test
    fun detailUpdated_beforeAnyEmission_theScreenIsNotLoaded() {
        assertFalse(HappeningState(now = NOW).isLoaded)
    }

    @Test
    fun detailUpdated_aRefreshReplacesTheFiche_withoutResettingTheClock() {
        val state = HappeningState(now = NOW, detail = detail(), isLoaded = true)

        val result = with(reducer) { state.reduce(HappeningMessage.DetailUpdated(detail(name = "DJ ALF (b2b)"))) }

        assertEquals("DJ ALF (b2b)", result.detail?.name)
        assertEquals(NOW, result.now)
    }

    @Test
    fun ticked_advancesTheInstantEveryPillIsMeasuredAgainst_andTouchesNothingElse() {
        val state = HappeningState(now = NOW, detail = detail(), isLoaded = true)
        val later = Instant.parse("2026-07-11T16:01:00+02:00")

        val result = with(reducer) { state.reduce(HappeningMessage.Ticked(later)) }

        assertEquals(later, result.now)
        assertEquals("DJ ALF", result.detail?.name)
        assertTrue(result.isLoaded)
    }

    private fun detail(name: String = "DJ ALF") =
        HappeningDetail(
            id = "dj-alf",
            name = name,
            categoryId = "musique",
            categoryName = "Musique",
            imageUrl = null,
            description = null,
            tags = emptyList(),
            dietary = emptyMap(),
            slots = emptyList(),
            price = null,
            bookingUrl = null,
            bookingRequired = false,
            equipmentProvided = null,
            suitability = null,
            supervised = null,
            menu = emptyList(),
            links = emptyList(),
            wishlisted = null,
        )

    private companion object {
        val NOW = Instant.parse("2026-07-11T16:00:00+02:00")
    }
}
