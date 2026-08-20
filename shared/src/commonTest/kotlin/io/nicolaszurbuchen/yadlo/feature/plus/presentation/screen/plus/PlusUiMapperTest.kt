package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus

import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloLinkMarkUiModel
import io.nicolaszurbuchen.yadlo.common.content.domain.model.SocialLink
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.socialIconFor
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
                PlusGroupIdUiModel.ON_SITE,
                PlusGroupIdUiModel.FESTIVAL,
                PlusGroupIdUiModel.INVOLVEMENT,
                PlusGroupIdUiModel.APP,
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
                PlusEntryUiModel.STANDS_FOOD,
                PlusEntryUiModel.STANDS_MAKERS,
                PlusEntryUiModel.PAYMENT,
                PlusEntryUiModel.ACCESS,
                PlusEntryUiModel.HOURS,
                PlusEntryUiModel.ASSISTANCE,
                PlusEntryUiModel.FAQ,
            ),
            rowsOf(PlusGroupIdUiModel.ON_SITE, published()),
        )
    }

    @Test
    fun toUiModel_theOtherThreeGroups_carryWhatTheContentSupports() {
        assertEquals(
            listOf(PlusEntryUiModel.STORY, PlusEntryUiModel.RESPONSIBLE, PlusEntryUiModel.PARTNERS),
            rowsOf(PlusGroupIdUiModel.FESTIVAL, published()),
        )
        assertEquals(
            listOf(PlusEntryUiModel.VOLUNTEERING, PlusEntryUiModel.CONTACT, PlusEntryUiModel.NEWSLETTER),
            rowsOf(PlusGroupIdUiModel.INVOLVEMENT, published()),
        )
        assertEquals(
            listOf(PlusEntryUiModel.ABOUT, PlusEntryUiModel.REPORT, PlusEntryUiModel.PRIVACY),
            rowsOf(PlusGroupIdUiModel.APP, published()),
        )
    }

    @Test
    fun toUiModel_aSectionThatWasNeverPublished_getsNoRow() {
        // The tab can never open a screen with nothing on it, which is what lets the whole of Plus
        // ship while half the festival's practical information is unwritten.
        assertTrue(PlusEntryUiModel.ACCESS !in rowsOf(PlusGroupIdUiModel.ON_SITE, published().copy(hasTransport = false)))
        assertTrue(PlusEntryUiModel.STANDS_FOOD !in rowsOf(PlusGroupIdUiModel.ON_SITE, published().copy(foodStandCount = 0)))
        assertTrue(PlusEntryUiModel.STANDS_MAKERS !in rowsOf(PlusGroupIdUiModel.ON_SITE, published().copy(makerStandCount = 0)))
        assertTrue(PlusEntryUiModel.STORY !in rowsOf(PlusGroupIdUiModel.FESTIVAL, published().copy(foundedYear = null)))
        assertTrue(PlusEntryUiModel.PARTNERS !in rowsOf(PlusGroupIdUiModel.FESTIVAL, published().copy(partnerCount = 0)))
        assertTrue(PlusEntryUiModel.NEWSLETTER !in rowsOf(PlusGroupIdUiModel.INVOLVEMENT, published().copy(newsletterUrl = null)))
    }

    @Test
    fun toUiModel_theStoryRow_writesTheYearTheFestivalStarted() {
        assertEquals(
            UiText.Resource(Res.string.plus_story_since, listOf("2015")),
            rowFor(PlusEntryUiModel.STORY, published())?.subtitle,
        )
    }

    @Test
    fun toUiModel_theCharterRow_namesTheChartersRatherThanRepeatingItsOwnTitle() {
        // "Charte FestiPlus" says more than "Festival responsable" does, and it comes out of the
        // content rather than out of a string.
        assertEquals(UiText.Raw("FestiPlus"), rowFor(PlusEntryUiModel.RESPONSIBLE, published())?.subtitle)
    }

    @Test
    fun toUiModel_twoCharters_readAsOneLine() {
        val overview = published().copy(charterNames = listOf("FestiPlus", "Charte du lac"))

        assertEquals(UiText.Raw("FestiPlus · Charte du lac"), rowFor(PlusEntryUiModel.RESPONSIBLE, overview)?.subtitle)
    }

    @Test
    fun toUiModel_theAppGroup_standsWithNoContentAtAll() {
        // À propos and Confidentialité are the app's own words, not the festival's. They are the
        // one part of this tab that cannot go missing when a publish does.
        assertEquals(listOf(PlusEntryUiModel.ABOUT, PlusEntryUiModel.PRIVACY), rowsOf(PlusGroupIdUiModel.APP, nothing()))
    }

    @Test
    fun toUiModel_nothingPublished_leavesOnlyTheAppGroup() {
        val model = PlusState(overview = nothing()).toUiModel()

        assertEquals(listOf(PlusGroupIdUiModel.APP), model.groups.map { it.id })
    }

    @Test
    fun toUiModel_theExternalRows_wearAMarkThatSaysWhereTheyGo() {
        // One leaves for the browser and one opens mail, against the chevron everything else wears.
        // On one bar of signal that is what tells someone whether tapping costs a page load.
        assertEquals(YadloLinkMarkUiModel.EXTERNAL, PlusEntryUiModel.NEWSLETTER.mark)
        assertEquals(YadloLinkMarkUiModel.MAIL, PlusEntryUiModel.REPORT.mark)
        assertEquals(YadloLinkMarkUiModel.DISCLOSURE, PlusEntryUiModel.STANDS_FOOD.mark)
    }

    @Test
    fun toUiModel_theNetworks_reachTheFooterWithTheirBundledMarks() {
        val model = PlusState(overview = published()).toUiModel()

        // Drawn at the foot rather than counted into a row that opened a screen of four links out.
        // The icon is resolved here, against the marks the app ships, so the row never renders an
        // empty square for a network the content added first.
        assertEquals(listOf(UiText.Raw("Instagram")), model.socials.map { it.name })
        assertEquals(socialIconFor("instagram"), model.socials.single().icon)
    }

    @Test
    fun toUiModel_noNetworksPublished_leavesNoFooterToDraw() {
        assertTrue(PlusState(overview = nothing()).toUiModel().socials.isEmpty())
    }

    @Test
    fun toUiModel_recruitingClosed_takesTheVolunteeringRowWithIt() {
        // A campaign rather than a permanent fact: in August there is nothing to sign up to, and a
        // row leading to a page that says so is worse than no row.
        assertTrue(
            PlusEntryUiModel.VOLUNTEERING !in rowsOf(PlusGroupIdUiModel.INVOLVEMENT, published().copy(hasVolunteering = false)),
        )
    }

    @Test
    fun toUiModel_eachStandsRow_countsItsOwnHalf() {
        // Two rows, two counts. One number over both would be the tab telling you six trucks are
        // waiting when two of them sell costumes.
        assertEquals(
            UiText.Resource(Res.string.plus_stands_count, listOf(6)),
            rowFor(PlusEntryUiModel.STANDS_FOOD, published())?.subtitle,
        )
        assertEquals(
            UiText.Resource(Res.string.plus_stands_count, listOf(2)),
            rowFor(PlusEntryUiModel.STANDS_MAKERS, published())?.subtitle,
        )
    }

    @Test
    fun toUiModel_cashRefused_isWrittenOnThePaymentRow() {
        val row = rowFor(PlusEntryUiModel.PAYMENT, published().copy(cashAccepted = false))

        assertEquals(UiText.Resource(Res.string.plus_payment_no_cash), row?.subtitle)
    }

    @Test
    fun toUiModel_cashTaken_saysNothingRatherThanSayingSoIsFine() {
        val row = rowFor(PlusEntryUiModel.PAYMENT, published().copy(cashAccepted = true))

        // That the site takes cards is not news. Only the refusal is worth a line someone has to
        // act on before leaving the house.
        assertNull(row?.subtitle)
    }

    @Test
    fun toUiModel_noPaymentSectionAtAll_removesTheRowRatherThanEmptyingIt() {
        assertTrue(PlusEntryUiModel.PAYMENT !in rowsOf(PlusGroupIdUiModel.ON_SITE, published().copy(cashAccepted = null)))
    }

    @Test
    fun toUiModel_theAssistanceRow_namesWhatWasMergedIntoIt() {
        val row = rowFor(PlusEntryUiModel.ASSISTANCE, published())

        // Three subjects behind one row needs the row to say so, or nobody opens it until they are
        // already looking for one of the three.
        assertEquals(UiText.Resource(Res.string.plus_assistance_subtitle), row?.subtitle)
    }

    @Test
    fun toUiModel_aFaqWithNoQuestions_getsNoRow() {
        assertTrue(PlusEntryUiModel.FAQ !in rowsOf(PlusGroupIdUiModel.ON_SITE, published().copy(faqCount = 0)))
    }

    @Test
    fun toUiModel_aGroupWithNoRowsLeft_isNotDrawnAsAnEmptyCard() {
        val model = PlusState(overview = nothing()).toUiModel()

        assertTrue(model.groups.none { it.id == PlusGroupIdUiModel.ON_SITE })
    }

    @Test
    fun toUiModel_readAndEmpty_isNotStillLoading() {
        assertTrue(!PlusState(overview = nothing()).toUiModel().isLoading)
    }

    private fun rowsOf(
        group: PlusGroupIdUiModel,
        overview: PlusOverview,
    ) = PlusState(overview = overview)
        .toUiModel()
        .groups
        .firstOrNull { it.id == group }
        ?.rows
        .orEmpty()
        .map { it.entry }

    private fun rowFor(
        entry: PlusEntryUiModel,
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
            hasOpeningHours = true,
            hasAssistance = true,
            faqCount = 1,
            foundedYear = 2015,
            charterNames = listOf("FestiPlus"),
            partnerCount = 39,
            hasVolunteering = true,
            hasContact = true,
            socials = listOf(SocialLink(id = "instagram", name = "Instagram", url = "https://example.ch/i")),
            newsletterUrl = "https://example.ch/newsletter",
            reportEmail = "hello@yadlo.ch",
        )

    private fun nothing() =
        PlusOverview(
            foodStandCount = 0,
            makerStandCount = 0,
            cashAccepted = null,
            hasTransport = false,
            hasOpeningHours = false,
            hasAssistance = false,
            faqCount = 0,
            foundedYear = null,
            charterNames = emptyList(),
            partnerCount = 0,
            hasVolunteering = false,
            hasContact = false,
            socials = emptyList(),
            newsletterUrl = null,
            reportEmail = null,
        )
}
