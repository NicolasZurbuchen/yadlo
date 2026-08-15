package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.accessibility

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.AccessibilityGuide
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AccessibilityReducerTest {
    private val reducer = AccessibilityStoreFactory.ReducerImpl

    @Test
    fun guideUpdated_beforeAnyEmission_hasNotLoaded() {
        assertFalse(AccessibilityState().hasLoaded)
        assertNull(AccessibilityState().guide)
    }

    @Test
    fun guideUpdated_aGuideWithNothingInIt_isStillAGuide() {
        val empty = AccessibilityGuide(available = emptyList(), unavailable = emptyList(), contactEmail = "hello@yadlo.ch")

        val result = with(reducer) { AccessibilityState().reduce(AccessibilityMessage.GuideUpdated(empty)) }

        // The state that ships. An empty guide is not the same as no guide, and flattening the two
        // here would lose the only address on the screen.
        assertTrue(result.hasLoaded)
        assertEquals("hello@yadlo.ch", result.guide?.contactEmail)
    }

    @Test
    fun guideUpdated_aNullSection_isLoadedRatherThanStillWaiting() {
        val result = with(reducer) { AccessibilityState().reduce(AccessibilityMessage.GuideUpdated(null)) }

        assertTrue(result.hasLoaded)
        assertNull(result.guide)
    }
}
