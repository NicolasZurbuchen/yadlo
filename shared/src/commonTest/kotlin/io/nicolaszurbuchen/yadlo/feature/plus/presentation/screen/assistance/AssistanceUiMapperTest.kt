package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance

import io.nicolaszurbuchen.yadlo.core.content.domain.model.Assistance
import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.AssistanceGuide
import io.nicolaszurbuchen.yadlo.infra.text.UiText
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
                guide = AssistanceGuide(numbers = listOf(number("144", "Ambulance")), recognition = emptyList(), lostPropertyEmail = null),
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
                guide = AssistanceGuide(numbers = emptyList(), recognition = emptyList(), lostPropertyEmail = null),
            ).toUiModel()

        assertEquals(UiText.Resource(Res.string.assistance_empty), model.emptyMessage)
    }

    @Test
    fun toUiModel_recognition_isCarriedAsTheContentWroteIt() {
        // The 160 is a fact about one edition, so it is authored rather than derived from an
        // archived figure — and the mapper's job is to not touch it.
        assertEquals(listOf("T-shirts Hot'Staff"), loaded().recognition)
    }

    private fun loaded() =
        AssistanceState(
            hasLoaded = true,
            guide =
                AssistanceGuide(
                    recognition =
                        listOf(Assistance.Recognition(id = "tshirts", text = "T-shirts Hot'Staff")),
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
