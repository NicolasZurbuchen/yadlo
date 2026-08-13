package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import app.cash.turbine.test
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Category
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentBundle
import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Edition
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Festival
import io.nicolaszurbuchen.yadlo.common.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Slot
import io.nicolaszurbuchen.yadlo.common.content.domain.model.SocialLink
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Venue
import io.nicolaszurbuchen.yadlo.common.content.domain.usecase.DerivePhaseUseCase
import io.nicolaszurbuchen.yadlo.feature.home.domain.usecase.ObserveHomeContentUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Instant

/** Settable so a test can sit at 23:59 on the eve of the festival and watch the Phase turn. */
private class SettableClock(
    var instant: Instant,
) : Clock {
    override fun now(): Instant = instant
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeExecutorTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        // MVIKotlin's CoroutineExecutor/Bootstrapper default to Dispatchers.Main;
        // this makes the executor's launched coroutines controllable via the test
        // dispatcher instead of real wall-clock scheduling.
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // region bootstrap / ObserveContent

    @Test
    fun onCreate_repositoryPublishesABundle_narrowsItIntoStateAndDerivesThePhase() =
        homeTest(startingAt = A_WEEK_AFTER) { store, repository, _ ->
            repository.emitStatus(ContentStatus.Ready(bundle = bundle(), updateRequired = false))
            testDispatcher.scheduler.runCurrent()

            assertEquals("Yadlo 2026", store.state.content?.editionName)
            assertEquals(PhaseUiModel.ENDED, store.state.phase)
        }

    @Test
    fun onCreate_nothingPublishedYet_holdsTheInitialPhaseAndNoContent() =
        homeTest(startingAt = SIX_DAYS_BEFORE) { store, _, _ ->
            testDispatcher.scheduler.runCurrent()

            assertEquals(null, store.state.content)
            assertEquals(PhaseUiModel.OFF_SEASON, store.state.phase)
        }

    @Test
    fun onCreate_editionHasNoSlots_staysOffSeasonRatherThanClaimingAProgramme() =
        homeTest(startingAt = SIX_DAYS_BEFORE) { store, repository, _ ->
            repository.emitStatus(
                ContentStatus.Ready(bundle = bundle(slots = emptyList()), updateRequired = false),
            )
            testDispatcher.scheduler.runCurrent()

            assertEquals(PhaseUiModel.OFF_SEASON, store.state.phase)
        }

    // endregion

    // region ticking

    @Test
    fun tick_theClockCrossesMidnightOnTheOpeningDay_movesThePhaseToLive() =
        homeTest(startingAt = JUST_BEFORE_MIDNIGHT) { store, repository, clock ->
            repository.emitStatus(ContentStatus.Ready(bundle = bundle(), updateRequired = false))
            testDispatcher.scheduler.runCurrent()
            assertEquals(PhaseUiModel.APPROACHING, store.state.phase)

            clock.instant = JUST_AFTER_MIDNIGHT
            testDispatcher.scheduler.advanceTimeBy(TWO_TICKS_MILLIS)
            testDispatcher.scheduler.runCurrent()

            assertEquals(PhaseUiModel.LIVE, store.state.phase)
        }

    @Test
    fun tick_advancesTheInstantTheCountdownIsMeasuredAgainst() =
        homeTest(startingAt = SIX_DAYS_BEFORE) { store, _, clock ->
            testDispatcher.scheduler.runCurrent()
            assertEquals(SIX_DAYS_BEFORE, store.state.now)

            clock.instant = JUST_BEFORE_MIDNIGHT
            testDispatcher.scheduler.advanceTimeBy(TWO_TICKS_MILLIS)
            testDispatcher.scheduler.runCurrent()

            assertEquals(JUST_BEFORE_MIDNIGHT, store.state.now)
        }

    // endregion

    // region HeroClicked

    @Test
    fun heroClicked_whileApproaching_sendsTheVisitorToTheProgrammeRatherThanAnEmptyPlan() =
        homeTest(startingAt = SIX_DAYS_BEFORE) { store, repository, _ ->
            repository.emitStatus(ContentStatus.Ready(bundle = bundle(), updateRequired = false))
            testDispatcher.scheduler.runCurrent()
            assertEquals(PhaseUiModel.APPROACHING, store.state.phase)

            store.labels.test {
                store.accept(HomeIntent.HeroClicked)
                assertEquals(HomeLabel.NavigateToProgramme, awaitItem())
            }
        }

    @Test
    fun heroClicked_whileAnnounced_sendsTheVisitorToTheProgramme() =
        homeTest(startingAt = A_MONTH_BEFORE) { store, repository, _ ->
            repository.emitStatus(ContentStatus.Ready(bundle = bundle(), updateRequired = false))
            testDispatcher.scheduler.runCurrent()
            assertEquals(PhaseUiModel.ANNOUNCED, store.state.phase)

            store.labels.test {
                store.accept(HomeIntent.HeroClicked)
                assertEquals(HomeLabel.NavigateToProgramme, awaitItem())
            }
        }

    // endregion

    // region AnnouncementClicked

    @Test
    fun announcementClicked_publishesTheUrlForThePlatformToOpen() =
        homeTest(startingAt = A_WEEK_AFTER) { store, _, _ ->
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(HomeIntent.AnnouncementClicked("https://example.com/aftermovie"))
                assertEquals(HomeLabel.OpenUrl("https://example.com/aftermovie"), awaitItem())
            }
        }

    // region SocialClicked

    @Test
    fun socialClicked_publishesTheUrlForThePlatformToOpen() =
        homeTest(startingAt = A_WEEK_AFTER) { store, _, _ ->
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(HomeIntent.SocialClicked("https://www.instagram.com/yadlo_ch/"))
                assertEquals(HomeLabel.OpenUrl("https://www.instagram.com/yadlo_ch/"), awaitItem())
            }
        }

    // endregion

    /**
     * The ticker never stops on its own, so every store is disposed even when an assertion throws —
     * an undisposed one leaves a repeating task on the shared test scheduler.
     */
    private fun homeTest(
        startingAt: Instant,
        block: suspend TestScope.(HomeStore, FakeContentRepository, SettableClock) -> Unit,
    ) = runTest {
        val repository = FakeContentRepository()
        val clock = SettableClock(startingAt)
        val store =
            HomeStoreFactory(
                storeFactory = DefaultStoreFactory(),
                observeHomeContent = ObserveHomeContentUseCase(repository),
                derivePhase = DerivePhaseUseCase(clock),
                clock = clock,
            ).create()

        try {
            block(store, repository, clock)
        } finally {
            store.dispose()
        }
    }

    private fun bundle(slots: List<Slot> = listOf(slot())) =
        ContentBundle(
            festival =
                Festival(
                    name = "Yadlo",
                    tagline = "Mouille ton corps, arrose ton esprit",
                    currentEditionId = "2026",
                    minSupportedAppVersion = null,
                    social = listOf(SocialLink(id = "instagram", name = "Instagram", url = "https://example.ch/insta")),
                ),
            edition =
                Edition(
                    id = "2026",
                    year = 2026,
                    name = "Yadlo 2026",
                    venue =
                        Venue(
                            name = "Plage de Préverenges",
                            address = "Route de la Plage, 1028 Préverenges",
                            latitude = 46.51,
                            longitude = 6.53,
                            provenance = Provenance.CONFIRMED,
                        ),
                    days = listOf(friday()),
                    categories = emptyList(),
                    happenings = listOf(happening()),
                    slots = slots,
                    partners = emptyList(),
                    figures = emptyList(),
                ),
            announcements = emptyList(),
        )

    private fun friday() =
        FestivalDay(
            id = "2026:fri",
            name = "Vendredi",
            date = "2026-07-10",
            start = Instant.parse("2026-07-10T16:00:00+02:00"),
            end = Instant.parse("2026-07-11T02:00:00+02:00"),
            provenance = Provenance.CONFIRMED,
        )

    private fun happening() =
        Happening.Artist(
            id = "2026:dubside",
            name = "Dubside",
            category = Category(id = "musique", name = "Musique", order = 1),
            description = null,
            images = emptyList(),
            provenance = Provenance.CONFIRMED,
            genres = emptyList(),
            links = emptyList(),
        )

    private fun slot() =
        Slot(
            id = "2026:dubside-fri",
            happening = happening(),
            day = friday(),
            start = Instant.parse("2026-07-10T20:00:00+02:00"),
            end = Instant.parse("2026-07-10T21:00:00+02:00"),
            provenance = Provenance.CONFIRMED,
        )

    private companion object {
        val A_MONTH_BEFORE = Instant.parse("2026-06-08T12:00:00+02:00")
        val SIX_DAYS_BEFORE = Instant.parse("2026-07-04T12:00:00+02:00")
        val JUST_BEFORE_MIDNIGHT = Instant.parse("2026-07-09T23:59:50+02:00")
        val JUST_AFTER_MIDNIGHT = Instant.parse("2026-07-10T00:00:05+02:00")
        val A_WEEK_AFTER = Instant.parse("2026-07-20T12:00:00+02:00")

        /** Two, so the assertion does not depend on which side of a single tick the advance lands. */
        const val TWO_TICKS_MILLIS = 120_000L
    }
}
