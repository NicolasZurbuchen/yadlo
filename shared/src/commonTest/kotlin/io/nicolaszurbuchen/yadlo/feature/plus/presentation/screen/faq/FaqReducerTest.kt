package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.faq

import io.nicolaszurbuchen.yadlo.core.content.domain.model.FaqEntry
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Provenance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FaqReducerTest {
    private val reducer = FaqStoreFactory.ReducerImpl

    @Test
    fun faqUpdated_beforeAnyEmission_thereIsNoListRatherThanAnEmptyOne() {
        assertNull(FaqState().entries)
    }

    @Test
    fun faqUpdated_firstEmission_holdsTheQuestions() {
        val result = with(reducer) { FaqState().reduce(FaqMessage.FaqUpdated(listOf(entree()))) }

        assertEquals(listOf("entree"), result.entries?.map { it.id })
    }

    @Test
    fun faqUpdated_aQuestionWithdrawnMidFestival_becomesAnEmptyListNotANullOne() {
        val state = FaqState(entries = listOf(entree()))

        val result = with(reducer) { state.reduce(FaqMessage.FaqUpdated(emptyList())) }

        assertTrue(result.entries?.isEmpty() == true)
    }

    private fun entree() =
        FaqEntry(
            id = "entree",
            question = "L'entrée est-elle payante ?",
            answer = "Non.",
            provenance = Provenance.CONFIRMED,
        )
}
