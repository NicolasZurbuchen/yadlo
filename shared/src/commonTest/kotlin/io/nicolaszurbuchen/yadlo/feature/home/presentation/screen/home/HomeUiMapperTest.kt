package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Announcement
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Figure
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.SocialLink
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.socialIconFor
import io.nicolaszurbuchen.yadlo.feature.home.domain.model.HomeContent
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.home_countdown_days_remaining
import yadlo.shared.generated.resources.home_hero_announced_kicker
import yadlo.shared.generated.resources.home_hero_announced_title
import yadlo.shared.generated.resources.home_hero_approaching_kicker
import yadlo.shared.generated.resources.home_live_before_body
import yadlo.shared.generated.resources.home_live_before_title
import yadlo.shared.generated.resources.home_live_closed_body
import yadlo.shared.generated.resources.home_live_open_body
import yadlo.shared.generated.resources.home_live_over_title
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

class HomeUiMapperTest {
    // region loading

    @Test
    fun toUiModel_noContentYet_isLoadingWithNoBlocks() {
        val state = HomeState(now = THREE_DAYS_BEFORE, phase = PhaseUiModel.APPROACHING)

        val result = state.toUiModel()

        assertEquals(true, result.isLoading)
        assertEquals(emptyList(), result.blocks)
    }

    // endregion

    // region block stack per phase

    @Test
    fun toUiModel_offSeason_stacksCountdownThenAnnoncesThenQuickAccessThenLesReseaux() {
        val state = state(phase = PhaseUiModel.OFF_SEASON, now = THREE_DAYS_BEFORE)

        assertEquals(listOf("Countdown", "Announcements", "QuickAccess", "Social"), state.toUiModel().blockNames())
    }

    @Test
    fun toUiModel_announced_stacksCountdownThenHeroThenAnnoncesThenQuickAccessThenLesReseaux() {
        val state = state(phase = PhaseUiModel.ANNOUNCED, now = THREE_DAYS_BEFORE)

        assertEquals(listOf("Countdown", "Hero", "Announcements", "QuickAccess", "Social"), state.toUiModel().blockNames())
    }

    @Test
    fun toUiModel_approaching_endsOnTheAnnoncesRatherThanOfferingAWayOffTheApp() {
        // The one phase without the networks, and the prototype's call rather than an omission:
        // three days out is the only moment the screen has something for the reader to do.
        val state = state(phase = PhaseUiModel.APPROACHING, now = THREE_DAYS_BEFORE)

        assertEquals(listOf("Countdown", "Hero", "QuickAccess", "Announcements"), state.toUiModel().blockNames())
    }

    @Test
    fun toUiModel_live_isDeliberatelyThinAndCarriesOnlyAnnoncesAndLesReseaux() {
        val state = state(phase = PhaseUiModel.LIVE, now = DURING)

        assertEquals(listOf("Announcements", "Social"), state.toUiModel().blockNames())
    }

    @Test
    fun toUiModel_ended_stacksMerciThenLesChiffresThenAnnoncesThenQuickAccessThenLesReseaux() {
        val state = state(phase = PhaseUiModel.ENDED, now = AFTER)

        assertEquals(listOf("ThankYou", "Figures", "Announcements", "QuickAccess", "Social"), state.toUiModel().blockNames())
    }

    // endregion

    // region countdown

    @Test
    fun toUiModel_countdown_countsWholeDaysToTheOpeningDay() {
        val state = state(phase = PhaseUiModel.APPROACHING, now = THREE_DAYS_BEFORE)

        val countdown = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Countdown>().single()

        assertEquals(UiText.Resource(Res.string.home_countdown_days_remaining, listOf("3")), countdown.daysText)
    }

    @Test
    fun toUiModel_lateInTheEvening_readsTheSameDayCountAsThatMorning() {
        // A calendar count, not a Duration divided by 24 hours: at 23:00 on the same date the
        // festival is still three sleeps away, and the screen must not quietly say two.
        val morning = state(phase = PhaseUiModel.APPROACHING, now = THREE_DAYS_BEFORE)
        val lateEvening = state(phase = PhaseUiModel.APPROACHING, now = THREE_DAYS_BEFORE_LATE)

        assertEquals(
            morning.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Countdown>().single().daysText,
            lateEvening.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Countdown>().single().daysText,
        )
    }

    @Test
    fun toUiModel_countdown_namesTheEditionAndTheVenueRatherThanRepeatingTheAppBarDates() {
        val state = state(phase = PhaseUiModel.APPROACHING, now = THREE_DAYS_BEFORE)

        val countdown = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Countdown>().single()

        assertEquals("Yadlo 2026 · Plage de Préverenges", countdown.subtitle)
    }

    @Test
    fun toUiModel_theFirstDayHasAlreadyPassed_dropsTheCountdownRatherThanRunningItBackwards() {
        val state = state(phase = PhaseUiModel.OFF_SEASON, now = AFTER)

        assertEquals(listOf("Announcements", "QuickAccess", "Social"), state.toUiModel().blockNames())
    }

    @Test
    fun toUiModel_noDaysPublished_dropsTheCountdown() {
        val state = state(phase = PhaseUiModel.OFF_SEASON, now = THREE_DAYS_BEFORE, days = emptyList())

        assertEquals(listOf("Announcements", "QuickAccess", "Social"), state.toUiModel().blockNames())
    }

    // endregion

    // region hero

    @Test
    fun toUiModel_announced_heroAnnouncesTheProgrammeAndItsSize() {
        val state = state(phase = PhaseUiModel.ANNOUNCED, now = THREE_DAYS_BEFORE)

        val hero = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Hero>().single()

        assertEquals(UiText.Resource(Res.string.home_hero_announced_kicker), hero.kicker)
        assertEquals(UiText.Resource(Res.string.home_hero_announced_title, listOf("2026")), hero.title)
        assertIs<UiText.Resource>(hero.body)
        assertEquals(listOf("13", "17", "1"), hero.body.args)
    }

    @Test
    fun toUiModel_approaching_heroAsksForThePlanToBeBuilt() {
        val state = state(phase = PhaseUiModel.APPROACHING, now = THREE_DAYS_BEFORE)

        val hero = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Hero>().single()

        assertEquals(UiText.Resource(Res.string.home_hero_approaching_kicker), hero.kicker)
    }

    // endregion

    // region annonces

    @Test
    fun toUiModel_annonce_formatsItsDateInTheFestivalTimezone() {
        val state = state(phase = PhaseUiModel.OFF_SEASON, now = THREE_DAYS_BEFORE)

        val announcements = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Announcements>().single()

        assertEquals("02.06.2026", announcements.items.first().dateText)
    }

    @Test
    fun toUiModel_moreThanTwoAnnonces_showsTwoAndOffersTheRest() {
        val state =
            state(
                phase = PhaseUiModel.OFF_SEASON,
                now = THREE_DAYS_BEFORE,
                announcements =
                    listOf(
                        announcement("un", PUBLISHED_EARLY),
                        announcement("deux", PUBLISHED_EARLY),
                        announcement("trois", PUBLISHED_EARLY),
                    ),
            )

        val announcements = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Announcements>().single()

        assertEquals(listOf("un", "deux"), announcements.items.map { it.id })
        assertEquals(true, announcements.hasMore)
    }

    @Test
    fun toUiModel_twoAnnoncesOrFewer_hidesTheActionBecauseThereIsNoRestToSee() {
        val state = state(phase = PhaseUiModel.OFF_SEASON, now = THREE_DAYS_BEFORE)

        val announcements = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Announcements>().single()

        assertEquals(2, announcements.items.size)
        assertEquals(false, announcements.hasMore)
    }

    @Test
    fun toUiModel_annonceWithNoBody_carriesAnEmptyStringRatherThanNull() {
        val state = state(phase = PhaseUiModel.OFF_SEASON, now = THREE_DAYS_BEFORE)

        val announcements = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Announcements>().single()

        assertEquals("", announcements.items.first().body)
    }

    @Test
    fun toUiModel_annonceWithNoUrl_staysUntappable() {
        val state = state(phase = PhaseUiModel.OFF_SEASON, now = THREE_DAYS_BEFORE)

        val announcements = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Announcements>().single()

        assertNull(announcements.items.single { it.id == "programme" }.url)
    }

    @Test
    fun toUiModel_live_keepsOnlyTheAnnoncesOfTheLastDay() {
        val state = state(phase = PhaseUiModel.LIVE, now = DURING)

        val announcements = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Announcements>().single()

        assertEquals(listOf("ce-matin"), announcements.items.map { it.id })
    }

    @Test
    fun toUiModel_liveWithNothingPublishedToday_dropsTheAnnoncesBlockAndKeepsTheReseaux() {
        val state =
            state(
                phase = PhaseUiModel.LIVE,
                now = DURING,
                announcements = listOf(announcement("vieille", PUBLISHED_EARLY)),
            )

        assertEquals(listOf("Social"), state.toUiModel().blockNames())
    }

    // endregion

    // region les réseaux

    @Test
    fun toUiModel_social_carriesEveryNetworkTheContentPublishes() {
        val state = state(phase = PhaseUiModel.OFF_SEASON, now = THREE_DAYS_BEFORE)

        val social = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Social>().single()

        assertEquals(
            listOf("Instagram", "Facebook", "YouTube", "TikTok").map { UiText.Raw(it) },
            social.items.map { it.name },
        )
        assertEquals(socialIconFor("instagram"), social.items.first().icon)
        assertEquals("https://example.com/instagram", social.items.first().url)
    }

    @Test
    fun toUiModel_aNetworkTheAppShipsNoMarkFor_stillRendersUnderItsName() {
        // The content can add a network before the app ships its icon. Dropping it would lose
        // published content silently; a null icon is the card's cue to print the name instead.
        val state =
            state(
                phase = PhaseUiModel.OFF_SEASON,
                now = THREE_DAYS_BEFORE,
                social = listOf(SocialLink("mastodon", "Mastodon", "https://example.com/mastodon")),
            )

        val social = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Social>().single()

        assertEquals(UiText.Raw("Mastodon"), social.items.single().name)
        assertNull(social.items.single().icon)
    }

    @Test
    fun toUiModel_noNetworksPublished_dropsTheBlock() {
        val state = state(phase = PhaseUiModel.OFF_SEASON, now = THREE_DAYS_BEFORE, social = emptyList())

        assertEquals(listOf("Countdown", "Announcements", "QuickAccess"), state.toUiModel().blockNames())
    }

    // endregion

    // region figures

    @Test
    fun toUiModel_figuresFromAPastEdition_carryTheProvenanceCaveat() {
        val state = state(phase = PhaseUiModel.ENDED, now = AFTER, figuresAreConfirmed = false)

        val figures = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Figures>().single()

        assertNotNull(figures.caveat)
    }

    @Test
    fun toUiModel_figuresConfirmedForThisEdition_carryNoCaveat() {
        val state = state(phase = PhaseUiModel.ENDED, now = AFTER, figuresAreConfirmed = true)

        val figures = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Figures>().single()

        assertNull(figures.caveat)
    }

    @Test
    fun toUiModel_endedWithNoFiguresPublished_dropsTheFiguresBlock() {
        val state = state(phase = PhaseUiModel.ENDED, now = AFTER, figures = emptyList())

        assertEquals(listOf("ThankYou", "Announcements", "QuickAccess", "Social"), state.toUiModel().blockNames())
    }

    @Test
    fun toUiModel_endedWithFigures_carriesThemThroughUnformatted() {
        val state = state(phase = PhaseUiModel.ENDED, now = AFTER)

        val figures = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Figures>().single()

        assertEquals(listOf("6000"), figures.items.map { it.value })
        assertEquals(listOf("visiteurs"), figures.items.map { it.label })
    }

    // endregion

    // region the live hero

    @Test
    fun toUiModel_liveBeforeTheGates_saysWhenTheSiteOpensAndOffersTheProgramme() {
        val state = state(phase = PhaseUiModel.LIVE, now = DURING, siteMoment = SiteMomentUiModel.BeforeFirstDay(GATES_OPEN))

        val hero = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Hero>().single()

        assertEquals(UiText.Resource(Res.string.home_live_before_title), hero.title)
        assertEquals(UiText.Resource(Res.string.home_live_before_body, listOf("16:00")), hero.body)
        assertTrue(hero.opensProgramme)
    }

    @Test
    fun toUiModel_liveAndOpen_saysHowLongIsLeftRatherThanJustPointingAtTheOtherTab() {
        val closesAt = Instant.parse("2026-07-12T02:00:00+02:00")
        val state = state(phase = PhaseUiModel.LIVE, now = DURING, siteMoment = SiteMomentUiModel.Open(closesAt))

        val hero = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Hero>().single()

        assertEquals(UiText.Resource(Res.string.home_live_open_body, listOf("02:00")), hero.body)
    }

    @Test
    fun toUiModel_liveAndShutForTheNight_namesTheReopening() {
        val reopensAt = Instant.parse("2026-07-12T12:00:00+02:00")
        val state = state(phase = PhaseUiModel.LIVE, now = DURING, siteMoment = SiteMomentUiModel.Closed(reopensAt))

        val hero = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Hero>().single()

        assertEquals(UiText.Resource(Res.string.home_live_closed_body, listOf("12:00")), hero.body)
        assertTrue(hero.opensProgramme)
    }

    @Test
    fun toUiModel_liveAndFinished_saysGoodbyeAndOffersNothingToTap() {
        // The half-step before ENDED. There is no programme left to open, and a block that looks
        // tappable and is not is worse than one that never offered.
        val state = state(phase = PhaseUiModel.LIVE, now = DURING, siteMoment = SiteMomentUiModel.Finished)

        val hero = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Hero>().single()

        assertEquals(UiText.Resource(Res.string.home_live_over_title), hero.title)
        assertEquals(false, hero.opensProgramme)
    }

    @Test
    fun toUiModel_live_leadsWithTheHeroAndKeepsTheAnnoncesUnderIt() {
        val state = state(phase = PhaseUiModel.LIVE, now = DURING, siteMoment = SiteMomentUiModel.Finished)

        assertEquals(listOf("Hero", "Announcements", "Social"), state.toUiModel().blockNames())
    }

    @Test
    fun toUiModel_liveWithNoDaysPublished_drawsNoHeroRatherThanAnEmptyOne() {
        val state = state(phase = PhaseUiModel.LIVE, now = DURING, siteMoment = null)

        assertEquals(listOf("Announcements", "Social"), state.toUiModel().blockNames())
    }

    @Test
    fun toUiModel_outsideLive_ignoresTheSiteMomentEntirely() {
        // It is derived whenever days exist, so it is set well outside the festival too. Only LIVE
        // reads it, and OFF_SEASON must not grow a hero out of it.
        val state = state(phase = PhaseUiModel.OFF_SEASON, now = THREE_DAYS_BEFORE, siteMoment = SiteMomentUiModel.Finished)

        assertTrue(state.toUiModel().blocks.none { it is HomeBlockUiModel.Hero })
    }

    // endregion

    // region quick access

    @Test
    fun toUiModel_offSeason_promotesTheThreeThingsThereIsTimeToActOnInNovember() {
        val state = state(phase = PhaseUiModel.OFF_SEASON, now = THREE_DAYS_BEFORE)

        assertEquals(
            listOf(
                QuickAccessEntryUiModel.CONTACT,
                QuickAccessEntryUiModel.NEWSLETTER,
                QuickAccessEntryUiModel.STORY,
            ),
            state.quickAccessEntries(),
        )
    }

    @Test
    fun toUiModel_recruitingIsPromotedOnlyOnceThereIsAnEditionToStaff() {
        // The split between the two long phases. Off season there is nothing to volunteer *for*
        // yet, so the offer is a way to reach the association; once the programme exists it becomes
        // an edition that has to be staffed, and that is the phase they are recruiting in.
        val offSeason = state(phase = PhaseUiModel.OFF_SEASON, now = THREE_DAYS_BEFORE).quickAccessEntries()
        val announced = state(phase = PhaseUiModel.ANNOUNCED, now = THREE_DAYS_BEFORE).quickAccessEntries()

        assertTrue(QuickAccessEntryUiModel.CONTACT in offSeason)
        assertTrue(QuickAccessEntryUiModel.VOLUNTEERING !in offSeason)
        assertTrue(QuickAccessEntryUiModel.VOLUNTEERING in announced)
        assertTrue(QuickAccessEntryUiModel.CONTACT !in announced)
    }

    @Test
    fun toUiModel_announced_promotesExactlyOneThingBecauseTheHeroIsDoingTheWork() {
        // The phase has one job — announce the programme and send people to it — so a grid of tiles
        // under the hero would be competing with the only thing the screen is for.
        val state = state(phase = PhaseUiModel.ANNOUNCED, now = THREE_DAYS_BEFORE)

        assertEquals(listOf(QuickAccessEntryUiModel.VOLUNTEERING), state.quickAccessEntries())
    }

    @Test
    fun toUiModel_approaching_promotesPaiementFirstThenCommentVenir() {
        // Order matters here and nowhere else: the payment rule is the one fact in the app that is
        // actionable *only* before leaving the house.
        val state = state(phase = PhaseUiModel.APPROACHING, now = THREE_DAYS_BEFORE)

        assertEquals(
            listOf(QuickAccessEntryUiModel.PAYMENT, QuickAccessEntryUiModel.ACCESS),
            state.quickAccessEntries(),
        )
    }

    @Test
    fun toUiModel_live_promotesNothingAtAll() {
        // DECISIONS.md § Accueil, block by block: the plan du site and the stands were turned down
        // here by name, and the app is meant to open on Programme during the festival anyway.
        val state = state(phase = PhaseUiModel.LIVE, now = DURING)

        assertTrue(state.toUiModel().blocks.none { it is HomeBlockUiModel.QuickAccess })
    }

    @Test
    fun toUiModel_ended_promotesTheNewsletterAlone() {
        val state = state(phase = PhaseUiModel.ENDED, now = AFTER)

        assertEquals(listOf(QuickAccessEntryUiModel.NEWSLETTER), state.quickAccessEntries())
    }

    @Test
    fun toUiModel_aPromotedSectionIsNotPublished_dropsThatTileAndKeepsTheRest() {
        val state = state(phase = PhaseUiModel.OFF_SEASON, now = THREE_DAYS_BEFORE, hasStory = false)

        assertEquals(
            listOf(QuickAccessEntryUiModel.CONTACT, QuickAccessEntryUiModel.NEWSLETTER),
            state.quickAccessEntries(),
        )
    }

    @Test
    fun toUiModel_noPromotedSectionIsPublished_dropsTheWholeBlockRatherThanDrawingAnEmptyOne() {
        val state =
            state(
                phase = PhaseUiModel.OFF_SEASON,
                now = THREE_DAYS_BEFORE,
                hasStory = false,
                hasContact = false,
                newsletterUrl = null,
            )

        assertEquals(listOf("Countdown", "Announcements", "Social"), state.toUiModel().blockNames())
    }

    @Test
    fun toUiModel_theNewsletterTile_carriesItsAddressAndTheOthersCarryNone() {
        // What the Route splits on. A tile with a url leaves the app; one without is a fixed
        // destination the navigator already knows.
        val state = state(phase = PhaseUiModel.OFF_SEASON, now = THREE_DAYS_BEFORE)

        val items = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.QuickAccess>().single().items

        assertEquals("https://example.com/newsletter", items.single { it.entry == QuickAccessEntryUiModel.NEWSLETTER }.url)
        assertTrue(items.filter { it.entry != QuickAccessEntryUiModel.NEWSLETTER }.all { it.url == null })
    }

    @Test
    fun toUiModel_eachPhase_titlesTheBlockForItself() {
        // The title is the block's argument — *Préparer sa venue* says why these two and why now —
        // so no two phases may reach for the same heading.
        val titles =
            listOf(PhaseUiModel.OFF_SEASON, PhaseUiModel.ANNOUNCED, PhaseUiModel.APPROACHING, PhaseUiModel.ENDED)
                .map { phase ->
                    state(phase = phase, now = THREE_DAYS_BEFORE)
                        .toUiModel()
                        .blocks
                        .filterIsInstance<HomeBlockUiModel.QuickAccess>()
                        .single()
                        .title
                }

        assertEquals(titles.size, titles.toSet().size)
    }

    // endregion

    @Test
    fun toUiModel_everyBlockTypeIsDistinctWithinAStack_soTheScreenCanKeyOnIt() {
        val names = state(phase = PhaseUiModel.ENDED, now = AFTER).toUiModel().blockNames()

        assertTrue(names.size == names.toSet().size)
    }

    @Test
    fun toUiModel_contentPresent_isNotLoading() {
        val result = state(phase = PhaseUiModel.ENDED, now = AFTER).toUiModel()

        assertEquals(false, result.isLoading)
        assertIs<HomeBlockUiModel.ThankYou>(result.blocks.first())
    }

    private fun HomeUiModel.blockNames(): List<String> = blocks.map { it::class.simpleName.orEmpty() }

    /** The promoted tiles in the order they are drawn. Fails loudly when the block is absent. */
    private fun HomeState.quickAccessEntries(): List<QuickAccessEntryUiModel> =
        toUiModel()
            .blocks
            .filterIsInstance<HomeBlockUiModel.QuickAccess>()
            .single()
            .items
            .map { it.entry }

    private fun state(
        phase: PhaseUiModel,
        now: Instant,
        days: List<FestivalDay> = listOf(friday()),
        announcements: List<Announcement> =
            listOf(
                announcement("programme", PUBLISHED_EARLY),
                announcement("ce-matin", PUBLISHED_DURING, url = "https://example.com"),
            ),
        figures: List<Figure> = listOf(Figure(id = "visiteurs", value = "6000", label = "visiteurs", provenance = Provenance.ARCHIVED)),
        figuresAreConfirmed: Boolean = false,
        social: List<SocialLink> =
            listOf(
                SocialLink("instagram", "Instagram", "https://example.com/instagram"),
                SocialLink("facebook", "Facebook", "https://example.com/facebook"),
                SocialLink("youtube", "YouTube", "https://example.com/youtube"),
                SocialLink("tiktok", "TikTok", "https://example.com/tiktok"),
            ),
        // Everything a Phase could promote is published by default, so a test about *which* tiles a
        // Phase gets is not silently also a test about what the content happens to hold. The tests
        // that care about an unpublished section turn one off by name.
        hasStory: Boolean = true,
        hasContact: Boolean = true,
        hasVolunteering: Boolean = true,
        hasTransport: Boolean = true,
        hasPayment: Boolean = true,
        newsletterUrl: String? = "https://example.com/newsletter",
        // Null by default so the phase-stack tests stay about the stack. The live-hero tests are
        // the ones that set it, and they are the only ones LIVE draws a hero for.
        siteMoment: SiteMomentUiModel? = null,
    ) = HomeState(
        now = now,
        phase = phase,
        siteMoment = siteMoment,
        content =
            HomeContent(
                editionName = "Yadlo 2026",
                editionYear = 2026,
                venueName = "Plage de Préverenges",
                days = days,
                hasPublishedProgramme = true,
                artistCount = 13,
                activityCount = 17,
                announcements = announcements,
                figures = figures,
                figuresAreConfirmed = figuresAreConfirmed,
                social = social,
                hasStory = hasStory,
                hasContact = hasContact,
                hasVolunteering = hasVolunteering,
                hasTransport = hasTransport,
                hasPayment = hasPayment,
                newsletterUrl = newsletterUrl,
            ),
    )

    private fun friday() =
        FestivalDay(
            id = "2026:fri",
            name = "Vendredi",
            date = "2026-07-10",
            start = GATES_OPEN,
            end = Instant.parse("2026-07-11T02:00:00+02:00"),
            provenance = Provenance.CONFIRMED,
        )

    private fun announcement(
        id: String,
        publishedAt: Instant,
        url: String? = null,
    ) = Announcement(
        id = id,
        publishedAt = publishedAt,
        title = id,
        body = null,
        editionId = "2026",
        url = url,
        provenance = Provenance.UNVERIFIED,
    )

    private companion object {
        val GATES_OPEN = Instant.parse("2026-07-10T16:00:00+02:00")

        val THREE_DAYS_BEFORE = Instant.parse("2026-07-07T09:00:00+02:00")

        /** The same calendar day as [THREE_DAYS_BEFORE], fourteen hours later. */
        val THREE_DAYS_BEFORE_LATE = Instant.parse("2026-07-07T23:00:00+02:00")

        val DURING = Instant.parse("2026-07-11T14:00:00+02:00")
        val AFTER = Instant.parse("2026-07-20T10:00:00+02:00")

        val PUBLISHED_EARLY = Instant.parse("2026-06-02T12:00:00+02:00")
        val PUBLISHED_DURING = Instant.parse("2026-07-11T09:00:00+02:00")
    }
}
