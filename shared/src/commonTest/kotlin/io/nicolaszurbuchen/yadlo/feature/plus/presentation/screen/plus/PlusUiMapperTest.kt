package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.PlusOverview
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.plus_assistance_subtitle
import yadlo.shared.generated.resources.plus_payment_no_cash
import yadlo.shared.generated.resources.plus_stands_count
import yadlo.shared.generated.resources.plus_story_since
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
    fun toUiModel_everythingPublished_offersTheFourGroupsInOrder() {
        val model = PlusState(overview = published()).toUiModel()

        // The groups follow what a reader is doing rather than the website's own menu: what serves
        // you on site, what tells you about the festival, what asks something of you, the app.
        assertEquals(
            listOf(
                PlusGroupUiId.ON_SITE,
                PlusGroupUiId.FESTIVAL,
                PlusGroupUiId.INVOLVEMENT,
                PlusGroupUiId.APP,
            ),
            model.groups.map { it.id },
        )
    }

    @Test
    fun toUiModel_theOnSiteGroup_putsPaymentThird() {
        val model = PlusState(overview = published()).toUiModel()

        // Deliberately third rather than buried: it is the most consequential fact the festival
        // publishes and the only one that has to be read before leaving the house.
        assertEquals(
            listOf(
                PlusEntry.STANDS_FOOD,
                PlusEntry.STANDS_MAKERS,
                PlusEntry.PAYMENT,
                PlusEntry.ACCESS,
                PlusEntry.ACCESSIBILITY,
                PlusEntry.HOURS,
                PlusEntry.ASSISTANCE,
                PlusEntry.FAQ,
            ),
            rowsOf(PlusGroupUiId.ON_SITE, published()),
        )
    }

    @Test
    fun toUiModel_theOtherThreeGroups_carryWhatTheContentSupports() {
        assertEquals(
            listOf(PlusEntry.STORY, PlusEntry.RESPONSIBLE, PlusEntry.PARTNERS),
            rowsOf(PlusGroupUiId.FESTIVAL, published()),
        )
        assertEquals(
            listOf(PlusEntry.CONTACT, PlusEntry.NEWSLETTER, PlusEntry.SOCIAL),
            rowsOf(PlusGroupUiId.INVOLVEMENT, published()),
        )
        assertEquals(
            listOf(PlusEntry.ABOUT, PlusEntry.REPORT, PlusEntry.PRIVACY),
            rowsOf(PlusGroupUiId.APP, published()),
        )
    }

    @Test
    fun toUiModel_aSectionThatWasNeverPublished_getsNoRow() {
        // The tab can never open a screen with nothing on it, which is what lets the whole of Plus
        // ship while half the festival's practical information is unwritten.
        assertTrue(PlusEntry.ACCESS !in rowsOf(PlusGroupUiId.ON_SITE, published().copy(hasTransport = false)))
        assertTrue(PlusEntry.STANDS_FOOD !in rowsOf(PlusGroupUiId.ON_SITE, published().copy(foodStandCount = 0)))
        assertTrue(PlusEntry.STANDS_MAKERS !in rowsOf(PlusGroupUiId.ON_SITE, published().copy(makerStandCount = 0)))
        assertTrue(PlusEntry.STORY !in rowsOf(PlusGroupUiId.FESTIVAL, published().copy(foundedYear = null)))
        assertTrue(PlusEntry.PARTNERS !in rowsOf(PlusGroupUiId.FESTIVAL, published().copy(partnerCount = 0)))
        assertTrue(PlusEntry.NEWSLETTER !in rowsOf(PlusGroupUiId.INVOLVEMENT, published().copy(newsletterUrl = null)))
    }

    @Test
    fun toUiModel_theStoryRow_writesTheYearTheFestivalStarted() {
        assertEquals(
            UiText.Resource(Res.string.plus_story_since, listOf("2015")),
            rowFor(PlusEntry.STORY, published())?.subtitle,
        )
    }

    @Test
    fun toUiModel_theCharterRow_namesTheChartersRatherThanRepeatingItsOwnTitle() {
        // "Charte FestiPlus" says more than "Festival responsable" does, and it comes out of the
        // content rather than out of a string.
        assertEquals(UiText.Raw("FestiPlus"), rowFor(PlusEntry.RESPONSIBLE, published())?.subtitle)
    }

    @Test
    fun toUiModel_twoCharters_readAsOneLine() {
        val overview = published().copy(charterNames = listOf("FestiPlus", "Charte du lac"))

        assertEquals(UiText.Raw("FestiPlus · Charte du lac"), rowFor(PlusEntry.RESPONSIBLE, overview)?.subtitle)
    }

    @Test
    fun toUiModel_theAppGroup_standsWithNoContentAtAll() {
        // À propos and Confidentialité are the app's own words, not the festival's. They are the
        // one part of this tab that cannot go missing when a publish does.
        assertEquals(listOf(PlusEntry.ABOUT, PlusEntry.PRIVACY), rowsOf(PlusGroupUiId.APP, nothing()))
    }

    @Test
    fun toUiModel_nothingPublished_leavesOnlyTheAppGroup() {
        val model = PlusState(overview = nothing()).toUiModel()

        assertEquals(listOf(PlusGroupUiId.APP), model.groups.map { it.id })
    }

    @Test
    fun toUiModel_theExternalRows_wearAMarkThatSaysWhereTheyGo() {
        // `↗` leaves for the browser and `✉` opens mail, against the chevron everything else wears.
        // On one bar of signal that is what tells someone whether tapping costs a page load.
        assertEquals("↗", PlusEntry.NEWSLETTER.mark)
        assertEquals("✉", PlusEntry.REPORT.mark)
        assertEquals("›", PlusEntry.STANDS_FOOD.mark)
    }

    @Test
    fun toUiModel_eachStandsRow_countsItsOwnHalf() {
        // Two rows, two counts. One number over both would be the tab telling you six trucks are
        // waiting when two of them sell costumes.
        assertEquals(
            UiText.Resource(Res.string.plus_stands_count, listOf(6)),
            rowFor(PlusEntry.STANDS_FOOD, published())?.subtitle,
        )
        assertEquals(
            UiText.Resource(Res.string.plus_stands_count, listOf(2)),
            rowFor(PlusEntry.STANDS_MAKERS, published())?.subtitle,
        )
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
        assertTrue(PlusEntry.PAYMENT !in rowsOf(PlusGroupUiId.ON_SITE, published().copy(cashAccepted = null)))
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
        assertTrue(PlusEntry.FAQ !in rowsOf(PlusGroupUiId.ON_SITE, published().copy(faqCount = 0)))
    }

    @Test
    fun toUiModel_accessibilityWithNothingPublished_stillGetsItsRow() {
        // The one section whose emptiness is the content. Hiding the row would hide the only
        // address a wheelchair user has to write to.
        assertTrue(PlusEntry.ACCESSIBILITY in rowsOf(PlusGroupUiId.ON_SITE, published().copy(hasAccessibility = true)))
    }

    @Test
    fun toUiModel_aGroupWithNoRowsLeft_isNotDrawnAsAnEmptyCard() {
        val model = PlusState(overview = nothing()).toUiModel()

        assertTrue(model.groups.none { it.id == PlusGroupUiId.ON_SITE })
    }

    @Test
    fun toUiModel_readAndEmpty_isNotStillLoading() {
        assertTrue(!PlusState(overview = nothing()).toUiModel().isLoading)
    }

    private fun rowsOf(
        group: PlusGroupUiId,
        overview: PlusOverview,
    ) = PlusState(overview = overview)
        .toUiModel()
        .groups
        .firstOrNull { it.id == group }
        ?.rows
        .orEmpty()
        .map { it.entry }

    private fun rowFor(
        entry: PlusEntry,
        overview: PlusOverview,
    ) = PlusState(overview = overview)
        .toUiModel()
        .groups
        .flatMap { it.rows }
        .firstOrNull { it.entry == entry }

    private fun published() =
        PlusOverview(
            foodStandCount = 6,
            makerStandCount = 2,
            cashAccepted = false,
            hasTransport = true,
            hasAccessibility = true,
            hasOpeningHours = true,
            hasAssistance = true,
            faqCount = 1,
            foundedYear = 2015,
            charterNames = listOf("FestiPlus"),
            partnerCount = 39,
            hasContact = true,
            socialCount = 4,
            newsletterUrl = "https://example.ch/newsletter",
            reportEmail = "hello@yadlo.ch",
        )

    private fun nothing() =
        PlusOverview(
            foodStandCount = 0,
            makerStandCount = 0,
            cashAccepted = null,
            hasTransport = false,
            hasAccessibility = false,
            hasOpeningHours = false,
            hasAssistance = false,
            faqCount = 0,
            foundedYear = null,
            charterNames = emptyList(),
            partnerCount = 0,
            hasContact = false,
            socialCount = 0,
            newsletterUrl = null,
            reportEmail = null,
        )
}
