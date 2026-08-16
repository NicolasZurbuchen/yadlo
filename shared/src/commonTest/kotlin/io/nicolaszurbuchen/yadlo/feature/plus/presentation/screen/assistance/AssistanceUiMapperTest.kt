package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Assistance
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.AssistanceGuide
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.assistance_empty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AssistanceUiMapperTest {
    @Test
    fun toUiModel_beforeTheBundleLands_isLoading() {
        assertTrue(AssistanceState().toUiModel().isLoading)
    }

    @Test
    fun toUiModel_noSectionAtAll_saysSo() {
        val model = AssistanceState(guide = null, hasLoaded = true).toUiModel()

        assertEquals(UiText.Resource(Res.string.assistance_empty), model.emptyMessage)
    }

    @Test
    fun toUiModel_numbersKeepTheOrderTheContentDeclares() {
        val model = loaded()

        assertEquals(listOf("112", "144", "117"), model.numbers.map { it.number })
    }

    @Test
    fun toUiModel_aNumbersLabel_survivesBecauseTheDigitsAloneSayNothing() {
        assertEquals("Ambulance", loaded().numbers[1].label)
    }

    @Test
    fun toUiModel_theNumberIsCarriedAsPublished() {
        // What gets dialled is stripped in the executor, so the two never have to be kept in step
        // and a number written "0800 14 14 14" still reads that way.
        assertEquals("112", loaded().numbers.first().number)
    }

    @Test
    fun toUiModel_theLostPropertyAddress_reachesTheScreen() {
        assertEquals("hello@yadlo.ch", loaded().lostPropertyEmail)
    }

    @Test
    fun toUiModel_numbersButNoAddress_isNotAnEmptyScreen() {
        val model =
            AssistanceState(
                hasLoaded = true,
                guide = AssistanceGuide(numbers = listOf(number("144", "Ambulance")), lostPropertyEmail = null),
            ).toUiModel()

        // The numbers are the half that needs nobody's confirmation. A broken reference elsewhere
        // must never blank them.
        assertNull(model.emptyMessage)
        assertEquals(1, model.numbers.size)
    }

    @Test
    fun toUiModel_aGuideWithNothingInItAtAll_saysSo() {
        val model =
            AssistanceState(
                hasLoaded = true,
                guide = AssistanceGuide(numbers = emptyList(), lostPropertyEmail = null),
            ).toUiModel()

        assertEquals(UiText.Resource(Res.string.assistance_empty), model.emptyMessage)
    }

    private fun loaded() =
        AssistanceState(
            hasLoaded = true,
            guide =
                AssistanceGuide(
                    numbers =
                        listOf(
                            number("112", "Urgences (numéro européen)"),
                            number("144", "Ambulance"),
                            number("117", "Police"),
                        ),
                    lostPropertyEmail = "hello@yadlo.ch",
                ),
        ).toUiModel()

    private fun number(
        number: String,
        label: String,
    ) = Assistance.EmergencyNumber(id = label.lowercase(), label = label, number = number)
}
