package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.faq

import io.nicolaszurbuchen.yadlo.core.content.domain.model.FaqEntry
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.faq_empty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FaqUiMapperTest {
    @Test
    fun toUiModel_beforeAnythingIsRead_isLoading() {
        val model = FaqState().toUiModel()

        assertTrue(model.isLoading)
        assertNull(model.emptyMessage)
    }

    @Test
    fun toUiModel_readAndEmpty_saysNoQuestionsArePublished() {
        assertEquals(UiText.Resource(Res.string.faq_empty), FaqState(entries = emptyList()).toUiModel().emptyMessage)
    }

    @Test
    fun toUiModel_carriesTheQuestionAndItsAnswerTogether() {
        val entry = FaqState(entries = listOf(entree())).toUiModel().entries.single()

        assertEquals("L'entrée est-elle payante ?", entry.question)
        assertEquals("Non. L'entrée est libre les trois jours.", entry.answer)
    }

    @Test
    fun toUiModel_keepsTheOrderTheAssociationPublishedThemIn() {
        val model = FaqState(entries = listOf(entree(), chiens())).toUiModel().entries

        assertEquals(listOf("entree", "chiens"), model.map { it.id })
    }

    @Test
    fun toUiModel_withSomethingPublished_saysNothingAboutBeingEmpty() {
        assertNull(FaqState(entries = listOf(entree())).toUiModel().emptyMessage)
    }

    private fun entree() =
        FaqEntry(
            id = "entree",
            question = "L'entrée est-elle payante ?",
            answer = "Non. L'entrée est libre les trois jours.",
            provenance = Provenance.CONFIRMED,
        )

    private fun chiens() =
        FaqEntry(
            id = "chiens",
            question = "Les chiens sont-ils admis ?",
            answer = "Oui, tenus en laisse.",
            provenance = Provenance.UNVERIFIED,
        )
}
