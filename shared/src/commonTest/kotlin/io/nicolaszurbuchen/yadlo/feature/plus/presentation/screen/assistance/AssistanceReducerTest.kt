package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Assistance
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.AssistanceGuide
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AssistanceReducerTest {
    private val reducer = AssistanceStoreFactory.ReducerImpl

    @Test
    fun guideUpdated_beforeAnyEmission_hasNotLoaded() {
        assertFalse(AssistanceState().hasLoaded)
        assertNull(AssistanceState().guide)
    }

    @Test
    fun guideUpdated_firstEmission_holdsTheNumbers() {
        val guide =
            AssistanceGuide(
                recognition = emptyList(),
                numbers = listOf(Assistance.EmergencyNumber(id = "ambulance", label = "Ambulance", number = "144")),
                lostPropertyEmail = "hello@yadlo.ch",
            )

        val result = with(reducer) { AssistanceState().reduce(AssistanceMessage.GuideUpdated(guide)) }

        assertTrue(result.hasLoaded)
        assertEquals(listOf("144"), result.guide?.numbers?.map { it.number })
    }

    @Test
    fun guideUpdated_aNullSection_isLoadedRatherThanStillWaiting() {
        val result = with(reducer) { AssistanceState().reduce(AssistanceMessage.GuideUpdated(null)) }

        assertTrue(result.hasLoaded)
        assertNull(result.guide)
    }
}
