package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.PlusOverview
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.plus_assistance_subtitle
import yadlo.shared.generated.resources.plus_payment_no_cash
import yadlo.shared.generated.resources.plus_stands_count
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlusUiMapperTest {
    @Test
    fun toUiModel_beforeTheBundleLands_isLoadingAndSaysNothingElse() {
        val model = PlusState().toUiModel()

        assertTrue(model.isLoading)
        assertTrue(model.groups.isEmpty())
    }

    @Test
    fun toUiModel_everythingPublished_offersEveryRowInOrder() {
        val model = PlusState(overview = published()).toUiModel()

        // Declaration order is display order, and payment is deliberately third: it is the most
        // consequential fact the festival publishes and the one its own site buries.
        assertEquals(
            listOf(
                PlusEntry.STANDS,
                PlusEntry.PAYMENT,
                PlusEntry.ACCESS,
                PlusEntry.ACCESSIBILITY,
                PlusEntry.HOURS,
                PlusEntry.ASSISTANCE,
                PlusEntry.FAQ,
            ),
            model.groups.single().rows.map { it.entry },
        )
    }

    @Test
    fun toUiModel_aSectionThatWasNeverPublished_getsNoRow() {
        val model = PlusState(overview = published().copy(hasTransport = false)).toUiModel()

        // The tab can never open a screen with nothing on it, which is what lets the whole of Plus
        // ship while half the festival's practical information is unwritten.
        assertTrue(model.groups.single().rows.none { it.entry == PlusEntry.ACCESS })
    }

    @Test
    fun toUiModel_noStandsPublished_getsNoRowEither() {
        val model = PlusState(overview = published().copy(standCount = 0)).toUiModel()

        assertTrue(model.groups.single().rows.none { it.entry == PlusEntry.STANDS })
    }

    @Test
    fun toUiModel_theStandsRow_countsThemOnTheRow() {
        val row = rowFor(PlusEntry.STANDS, published().copy(standCount = 8))

        assertEquals(UiText.Resource(Res.string.plus_stands_count, listOf(8)), row?.subtitle)
    }

    @Test
    fun toUiModel_cashRefused_isWrittenOnThePaymentRow() {
        val row = rowFor(PlusEntry.PAYMENT, published().copy(cashAccepted = false))

        assertEquals(UiText.Resource(Res.string.plus_payment_no_cash), row?.subtitle)
    }

    @Test
    fun toUiModel_cashTaken_saysNothingRatherThanSayingSoIsFine() {
        val row = rowFor(PlusEntry.PAYMENT, published().copy(cashAccepted = true))

        // That the site takes cards is not news. Only the refusal is worth a line someone has to
        // act on before leaving the house.
        assertNull(row?.subtitle)
    }

    @Test
    fun toUiModel_noPaymentSectionAtAll_removesTheRowRatherThanEmptyingIt() {
        val model = PlusState(overview = published().copy(cashAccepted = null)).toUiModel()

        assertTrue(model.groups.single().rows.none { it.entry == PlusEntry.PAYMENT })
    }

    @Test
    fun toUiModel_theAssistanceRow_namesWhatWasMergedIntoIt() {
        val row = rowFor(PlusEntry.ASSISTANCE, published())

        // Three subjects behind one row needs the row to say so, or nobody opens it until they are
        // already looking for one of the three.
        assertEquals(UiText.Resource(Res.string.plus_assistance_subtitle), row?.subtitle)
    }

    @Test
    fun toUiModel_aFaqWithNoQuestions_getsNoRow() {
        val model = PlusState(overview = published().copy(faqCount = 0)).toUiModel()

        assertTrue(model.groups.single().rows.none { it.entry == PlusEntry.FAQ })
    }

    @Test
    fun toUiModel_accessibilityWithNothingPublished_stillGetsItsRow() {
        val model = PlusState(overview = published().copy(hasAccessibility = true)).toUiModel()

        // The one section whose emptiness is the content. Hiding the row would hide the only
        // address a wheelchair user has to write to.
        assertTrue(model.groups.single().rows.any { it.entry == PlusEntry.ACCESSIBILITY })
    }

    @Test
    fun toUiModel_aGroupWithNoRowsLeft_isNotDrawnAsAnEmptyCard() {
        val model = PlusState(overview = nothing()).toUiModel()

        assertTrue(model.groups.isEmpty())
    }

    @Test
    fun toUiModel_readAndEmpty_isNotStillLoading() {
        assertTrue(!PlusState(overview = nothing()).toUiModel().isLoading)
    }

    private fun rowFor(
        entry: PlusEntry,
        overview: PlusOverview,
    ) = PlusState(overview = overview).toUiModel().groups.single().rows.firstOrNull { it.entry == entry }

    private fun published() =
        PlusOverview(
            standCount = 8,
            cashAccepted = false,
            hasTransport = true,
            hasAccessibility = true,
            hasOpeningHours = true,
            hasAssistance = true,
            faqCount = 1,
        )

    private fun nothing() =
        PlusOverview(
            standCount = 0,
            cashAccepted = null,
            hasTransport = false,
            hasAccessibility = false,
            hasOpeningHours = false,
            hasAssistance = false,
            faqCount = 0,
        )
}
