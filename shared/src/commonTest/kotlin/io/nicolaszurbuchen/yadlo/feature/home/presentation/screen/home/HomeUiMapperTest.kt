package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Announcement
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Figure
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.feature.home.domain.model.HomeContent
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.home_hero_announced_action
import yadlo.shared.generated.resources.home_hero_approaching_action
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

class HomeUiMapperTest {
    // region loading

    @Test
    fun toUiModel_noContentYet_isLoadingWithNoBlocks() {
        val state = HomeState(now = WEEK_BEFORE, phase = PhaseUiModel.APPROACHING)

        val result = state.toUiModel()

        assertEquals(true, result.isLoading)
        assertEquals(emptyList(), result.blocks)
    }

    // endregion

    // region block stack per phase

    @Test
    fun toUiModel_offSeason_stacksCountdownThenAnnonces() {
        val state = state(phase = PhaseUiModel.OFF_SEASON, now = WEEK_BEFORE)

        assertEquals(listOf("Countdown", "Announcements"), state.toUiModel().blockNames())
    }

    @Test
    fun toUiModel_announced_stacksCountdownThenHeroThenAnnonces() {
        val state = state(phase = PhaseUiModel.ANNOUNCED, now = WEEK_BEFORE)

        assertEquals(listOf("Countdown", "Hero", "Announcements"), state.toUiModel().blockNames())
    }

    @Test
    fun toUiModel_approaching_stacksCountdownThenHeroThenAnnonces() {
        val state = state(phase = PhaseUiModel.APPROACHING, now = WEEK_BEFORE)

        assertEquals(listOf("Countdown", "Hero", "Announcements"), state.toUiModel().blockNames())
    }

    @Test
    fun toUiModel_live_isDeliberatelyThinAndCarriesOnlyAnnonces() {
        val state = state(phase = PhaseUiModel.LIVE, now = DURING)

        assertEquals(listOf("Announcements"), state.toUiModel().blockNames())
    }

    @Test
    fun toUiModel_ended_stacksMerciThenLesChiffresThenAnnonces() {
        val state = state(phase = PhaseUiModel.ENDED, now = AFTER)

        assertEquals(listOf("ThankYou", "Figures", "Announcements"), state.toUiModel().blockNames())
    }

    // endregion

    // region countdown

    @Test
    fun toUiModel_countdown_splitsTheRemainingTimeToTheFirstDayIntoPaddedCells() {
        val state = state(phase = PhaseUiModel.APPROACHING, now = WEEK_BEFORE)

        val countdown = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Countdown>().single()

        assertEquals(listOf("6", "04", "12", "30"), countdown.cells.map { it.value })
        assertEquals("Yadlo 2026", countdown.editionName)
    }

    @Test
    fun toUiModel_theFirstDayHasAlreadyPassed_dropsTheCountdownRatherThanRunningItBackwards() {
        val state = state(phase = PhaseUiModel.OFF_SEASON, now = AFTER)

        assertEquals(listOf("Announcements"), state.toUiModel().blockNames())
    }

    @Test
    fun toUiModel_noDaysPublished_dropsTheCountdown() {
        val state = state(phase = PhaseUiModel.OFF_SEASON, now = WEEK_BEFORE, days = emptyList())

        assertEquals(listOf("Announcements"), state.toUiModel().blockNames())
    }

    // endregion

    // region hero

    @Test
    fun toUiModel_announced_heroPointsAtTheProgramme() {
        val state = state(phase = PhaseUiModel.ANNOUNCED, now = WEEK_BEFORE)

        val hero = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Hero>().single()

        assertEquals(UiText.Resource(Res.string.home_hero_announced_action), hero.actionLabel)
    }

    @Test
    fun toUiModel_approaching_heroPointsAtMonYadlo() {
        val state = state(phase = PhaseUiModel.APPROACHING, now = WEEK_BEFORE)

        val hero = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Hero>().single()

        assertEquals(UiText.Resource(Res.string.home_hero_approaching_action), hero.actionLabel)
    }

    // endregion

    // region annonces

    @Test
    fun toUiModel_annonce_formatsItsDateInTheFestivalTimezone() {
        val state = state(phase = PhaseUiModel.OFF_SEASON, now = WEEK_BEFORE)

        val announcements = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Announcements>().single()

        assertEquals("02.06.2026", announcements.items.first().dateText)
    }

    @Test
    fun toUiModel_annonceWithNoUrl_staysUntappable() {
        val state = state(phase = PhaseUiModel.OFF_SEASON, now = WEEK_BEFORE)

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
    fun toUiModel_liveWithNothingPublishedToday_dropsTheAnnoncesBlockEntirely() {
        val state = state(phase = PhaseUiModel.LIVE, now = DURING, announcements = listOf(announcement("vieille", PUBLISHED_EARLY)))

        assertEquals(emptyList(), state.toUiModel().blocks)
    }

    // endregion

    // region figures

    @Test
    fun toUiModel_endedWithNoFiguresPublished_dropsTheFiguresBlock() {
        val state = state(phase = PhaseUiModel.ENDED, now = AFTER, figures = emptyList())

        assertEquals(listOf("ThankYou", "Announcements"), state.toUiModel().blockNames())
    }

    @Test
    fun toUiModel_endedWithFigures_carriesThemThroughUnformatted() {
        val state = state(phase = PhaseUiModel.ENDED, now = AFTER)

        val figures = state.toUiModel().blocks.filterIsInstance<HomeBlockUiModel.Figures>().single()

        assertEquals(listOf("6000"), figures.items.map { it.value })
        assertEquals(listOf("visiteurs"), figures.items.map { it.label })
    }

    // endregion

    @Test
    fun toUiModel_everyBlockTypeIsDistinctWithinAStack_soTheScreenCanKeyOnIt() {
        val state = state(phase = PhaseUiModel.ENDED, now = AFTER)

        val names = state.toUiModel().blockNames()

        assertTrue(names.size == names.toSet().size)
    }

    @Test
    fun toUiModel_contentPresent_isNotLoading() {
        val result = state(phase = PhaseUiModel.ENDED, now = AFTER).toUiModel()

        assertEquals(false, result.isLoading)
        assertIs<HomeBlockUiModel.ThankYou>(result.blocks.first())
    }

    private fun HomeUiModel.blockNames(): List<String> = blocks.map { it::class.simpleName.orEmpty() }

    private fun state(
        phase: PhaseUiModel,
        now: Instant,
        days: List<FestivalDay> = listOf(friday()),
        announcements: List<Announcement> =
            listOf(
                announcement("programme", PUBLISHED_EARLY),
                announcement("ce-matin", PUBLISHED_DURING, url = "https://example.com"),
            ),
        figures: List<Figure> = listOf(Figure(id = "visiteurs", value = "6000", label = "visiteurs", provenance = Provenance.CONFIRMED)),
    ) = HomeState(
        now = now,
        phase = phase,
        content =
            HomeContent(
                editionName = "Yadlo 2026",
                days = days,
                hasPublishedProgramme = true,
                announcements = announcements,
                figures = figures,
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

        /** Six days, four hours, twelve minutes and thirty seconds before the gates open. */
        val WEEK_BEFORE = Instant.parse("2026-07-04T11:47:30+02:00")
        val DURING = Instant.parse("2026-07-11T14:00:00+02:00")
        val AFTER = Instant.parse("2026-07-20T10:00:00+02:00")

        val PUBLISHED_EARLY = Instant.parse("2026-06-02T12:00:00+02:00")
        val PUBLISHED_DURING = Instant.parse("2026-07-11T09:00:00+02:00")
    }
}
