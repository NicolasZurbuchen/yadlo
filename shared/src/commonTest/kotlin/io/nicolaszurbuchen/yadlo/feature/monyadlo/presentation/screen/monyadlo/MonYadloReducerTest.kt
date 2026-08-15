package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo

import io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.model.MonYadloContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant

class MonYadloReducerTest {
    private val reducer = MonYadloStoreFactory.ReducerImpl

    @Test
    fun contentUpdated_firstEmission_holdsThePlan() {
        val state = MonYadloState(now = NOW)

        val result = with(reducer) { state.reduce(MonYadloMessage.ContentUpdated(content(wishlistCount = 2))) }

        assertEquals(2, result.content?.wishlistCount)
    }

    @Test
    fun contentUpdated_beforeAnyEmission_thereIsNoPlanRatherThanAnEmptyOne() {
        // Null is "not read yet". An emptied Plan arrives as content with no days, which is a
        // different screen from one that has not loaded.
        assertNull(MonYadloState(now = NOW).content)
    }

    @Test
    fun contentUpdated_aHeartTappedElsewhere_replacesThePlanWithoutResettingTheClock() {
        val state = MonYadloState(now = NOW, content = content(wishlistCount = 2))

        val result = with(reducer) { state.reduce(MonYadloMessage.ContentUpdated(content(wishlistCount = 3))) }

        assertEquals(3, result.content?.wishlistCount)
        assertEquals(NOW, result.now)
    }

    @Test
    fun ticked_advancesTheInstantEveryPillIsMeasuredAgainst_andTouchesNothingElse() {
        val state = MonYadloState(now = NOW, content = content(wishlistCount = 1))
        val later = Instant.parse("2026-07-11T21:01:00+02:00")

        val result = with(reducer) { state.reduce(MonYadloMessage.Ticked(later)) }

        assertEquals(later, result.now)
        assertEquals(1, result.content?.wishlistCount)
    }

    private fun content(wishlistCount: Int) = MonYadloContent(days = emptyList(), wishlistCount = wishlistCount)

    private companion object {
        val NOW = Instant.parse("2026-07-11T21:00:00+02:00")
    }
}
