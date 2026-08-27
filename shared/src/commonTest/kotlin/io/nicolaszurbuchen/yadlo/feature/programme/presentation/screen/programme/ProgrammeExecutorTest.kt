package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import app.cash.turbine.test
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.nicolaszurbuchen.yadlo.core.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Category
import io.nicolaszurbuchen.yadlo.core.content.domain.model.ContentBundle
import io.nicolaszurbuchen.yadlo.core.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Edition
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Festival
import io.nicolaszurbuchen.yadlo.core.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Happening
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Slot
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Venue
import io.nicolaszurbuchen.yadlo.core.content.domain.usecase.DerivePhaseUseCase
import io.nicolaszurbuchen.yadlo.feature.programme.domain.usecase.ObserveProgrammeContentUseCase
import io.nicolaszurbuchen.yadlo.infra.time.AppClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

/**
 * Settable so a test can stand on the Saturday at 01:00 and check which day the screen opens on.
 *
 * Assigning [instant] moves the reading silently, which is what wall time does. [jumpTo] also
 * signals, which is what the debug time-travel panel does — the two are separate so a test that
 * means to exercise the ticker cannot be satisfied by the jump instead.
 */
private class SettableClock(
    var instant: Instant,
) : AppClock {
    private val jumpSignal = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    override val jumps: Flow<Unit> = jumpSignal

    override fun now(): Instant = instant

    fun jumpTo(target: Instant) {
        instant = target
        jumpSignal.tryEmit(Unit)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ProgrammeExecutorTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // region bootstrap and the day it opens on

    @Test
    fun onCreate_repositoryPublishesABundle_narrowsItIntoState() =
        programmeTest(startingAt = A_MONTH_BEFORE) { store, repository, _ ->
            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            assertEquals(3, store.state.content?.days?.size)
            assertEquals(listOf("2026:dubside-sat"), store.state.content?.slots?.map { it.id })
        }

    @Test
    fun onCreate_theWeekBefore_opensOnTheWholeWeekend() =
        programmeTest(startingAt = THREE_DAYS_BEFORE) { store, repository, _ ->
            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            // APPROACHING is the only time anyone realistically builds a Plan, and nobody builds one
            // a day at a time — DECISIONS.md § APPROACHING exists for one reason.
            assertEquals(ProgrammeScopeState.AllDays, store.state.selectedScope)
        }

    @Test
    fun onCreate_atOneInTheMorningOnSaturday_opensOnFridayBecauseFridayHasNotEnded() =
        programmeTest(startingAt = SATURDAY_SMALL_HOURS) { store, repository, _ ->
            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            // The FestivalDay window is the hours the site is open, and Friday's runs to 02:00.
            // Opening on Saturday would hide the set playing thirty metres away.
            assertEquals(ProgrammeScopeState.Day("2026:fri"), store.state.selectedScope)
        }

    @Test
    fun onCreate_inTheGapBetweenTwoDays_opensOnTheNextOneToOpen() =
        programmeTest(startingAt = SATURDAY_BEFORE_DAWN) { store, repository, _ ->
            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            // 04:00: Friday closed at 02:00 and Saturday opens at 12:00, so no day is current at
            // all. LIVE spans the gap, and the useful answer in it is the day about to start.
            assertEquals(ProgrammeScopeState.Day("2026:sat"), store.state.selectedScope)
        }

    @Test
    fun onCreate_afterTheFestival_opensOnTheWholeWeekend() =
        programmeTest(startingAt = A_WEEK_AFTER) { store, repository, _ ->
            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            // ENDED is read the way OFF_SEASON is — from a sofa, across all three days, remembering
            // rather than deciding.
            assertEquals(ProgrammeScopeState.AllDays, store.state.selectedScope)
        }

    @Test
    fun onCreate_nothingPublishedYet_holdsNoContentAndNoScope() =
        programmeTest(startingAt = A_MONTH_BEFORE) { store, _, _ ->
            testDispatcher.scheduler.runCurrent()

            assertEquals(null, store.state.content)
            assertEquals(null, store.state.selectedScope)
        }

    // endregion

    // region the scope it opens on

    @Test
    fun onCreate_theProgrammeHasJustDropped_opensOnTheCatalogue() =
        programmeTest(startingAt = A_MONTH_BEFORE) { store, repository, _ ->
            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            // ANNOUNCED. Nobody has read the bill yet, so the useful screen is the one that says
            // what there is rather than the one that says when it is.
            assertEquals(ProgrammeScopeState.Catalogue, store.state.selectedScope)
        }

    @Test
    fun onCreate_duringTheFestival_opensOnTheDayYouAreStandingIn() =
        programmeTest(startingAt = SATURDAY_AFTERNOON) { store, repository, _ ->
            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            // "What is on now" is the only question on site, and it is about one day.
            assertEquals(ProgrammeScopeState.Day("2026:sat"), store.state.selectedScope)
        }

    @Test
    fun thePhaseTurnsOverWhileTheAppIsOpen_doesNotMoveTheScopeUnderTheReader() =
        programmeTest(startingAt = A_MONTH_BEFORE) { store, repository, clock ->
            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()
            assertEquals(ProgrammeScopeState.Catalogue, store.state.selectedScope)

            // Midnight on J-7, with a content refresh landing on the other side of it. A start
            // scope, not a redirect — the distinction TabNavigator.selectStart exists for.
            clock.instant = THREE_DAYS_BEFORE
            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            assertEquals(ProgrammeScopeState.Catalogue, store.state.selectedScope)
        }

    @Test
    fun scopeSelected_switchesIt() =
        programmeTest(startingAt = SATURDAY_AFTERNOON) { store, repository, _ ->
            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            store.accept(ProgrammeIntent.ScopeSelected(ProgrammeScopeState.Catalogue.id))

            assertEquals(ProgrammeScopeState.Catalogue, store.state.selectedScope)

            store.accept(ProgrammeIntent.ScopeSelected(ProgrammeScopeState.AllDays.id))

            assertEquals(ProgrammeScopeState.AllDays, store.state.selectedScope)
        }

    @Test
    fun catalogueClicked_opensTheSameFicheTheTimetableDoes() =
        programmeTest(startingAt = A_MONTH_BEFORE) { store, repository, _ ->
            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            // One destination for a Happening however it was found, which is what keeps the
            // Catalogue a second way of looking rather than a second door.
            store.labels.test {
                store.accept(ProgrammeIntent.SlotClicked("dubside"))
                assertEquals(ProgrammeLabel.NavigateToHappening("dubside"), awaitItem())
            }
        }

    // endregion

    // region ticking

    @Test
    fun tick_advancesTheInstantEveryPillIsMeasuredAgainst() =
        programmeTest(startingAt = SATURDAY_AFTERNOON) { store, _, clock ->
            testDispatcher.scheduler.runCurrent()
            assertEquals(SATURDAY_AFTERNOON, store.state.now)

            clock.instant = SATURDAY_EVENING
            testDispatcher.scheduler.advanceTimeBy(TWO_TICKS_MILLIS)
            testDispatcher.scheduler.runCurrent()

            assertEquals(SATURDAY_EVENING, store.state.now)
        }

    @Test
    fun clockJumps_picksItUpWithoutWaitingOutTheTick() =
        programmeTest(startingAt = SATURDAY_AFTERNOON) { store, _, clock ->
            testDispatcher.scheduler.runCurrent()

            // What the debug time-travel panel does. A minute-long interval would make it useless
            // for checking a state that only exists for a few minutes.
            clock.jumpTo(SATURDAY_EVENING)
            testDispatcher.scheduler.runCurrent()

            assertEquals(SATURDAY_EVENING, store.state.now)
        }

    // endregion

    // region filters

    @Test
    fun categoryToggled_twice_returnsToShowingEverything() =
        programmeTest(startingAt = SATURDAY_AFTERNOON) { store, _, _ ->
            testDispatcher.scheduler.runCurrent()

            store.accept(ProgrammeIntent.CategoryToggled("musique"))
            assertEquals(setOf("musique"), store.state.selectedCategoryIds)

            // Deselecting the last chip is *Tout*, not a filter that excludes everything.
            store.accept(ProgrammeIntent.CategoryToggled("musique"))
            assertEquals(emptySet(), store.state.selectedCategoryIds)
        }

    @Test
    fun categoryToggled_twoDifferentChips_keepsBoth() =
        programmeTest(startingAt = SATURDAY_AFTERNOON) { store, _, _ ->
            testDispatcher.scheduler.runCurrent()

            store.accept(ProgrammeIntent.CategoryToggled("musique"))
            store.accept(ProgrammeIntent.CategoryToggled("enfants"))

            // "Musique et enfants" is the question a parent at a music festival actually has.
            assertEquals(setOf("musique", "enfants"), store.state.selectedCategoryIds)
        }

    @Test
    fun allCategoriesSelected_clearsWhateverWasChosen() =
        programmeTest(startingAt = SATURDAY_AFTERNOON) { store, _, _ ->
            testDispatcher.scheduler.runCurrent()
            store.accept(ProgrammeIntent.CategoryToggled("musique"))

            store.accept(ProgrammeIntent.AllCategoriesSelected)

            assertEquals(emptySet(), store.state.selectedCategoryIds)
        }

    @Test
    fun scopeSelected_aDayFromTheCatalogue_isHowYouLeaveIt() =
        programmeTest(startingAt = A_MONTH_BEFORE) { store, repository, _ ->
            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()
            assertEquals(ProgrammeScopeState.Catalogue, store.state.selectedScope)

            store.accept(ProgrammeIntent.ScopeSelected("2026:sun"))

            assertEquals(ProgrammeScopeState.Day("2026:sun"), store.state.selectedScope)
        }

    // endregion

    // region SlotClicked

    @Test
    fun slotClicked_opensTheHappeningsFicheRatherThanAScreenForTheSlot() =
        programmeTest(startingAt = SATURDAY_AFTERNOON) { store, _, _ ->
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(ProgrammeIntent.SlotClicked("dubside"))
                assertEquals(ProgrammeLabel.NavigateToHappening("dubside"), awaitItem())
            }
        }

    // endregion

    /** The ticker never stops on its own, so every store is disposed even when an assertion throws. */
    private fun programmeTest(
        startingAt: Instant,
        block: suspend TestScope.(ProgrammeStore, FakeContentRepository, SettableClock) -> Unit,
    ) = runTest {
        val repository = FakeContentRepository()
        val clock = SettableClock(startingAt)
        val store =
            ProgrammeStoreFactory(
                storeFactory = DefaultStoreFactory(),
                observeProgrammeContent = ObserveProgrammeContentUseCase(repository),
                derivePhase = DerivePhaseUseCase(clock),
                clock = clock,
            ).create()

        try {
            block(store, repository, clock)
        } finally {
            store.dispose()
        }
    }

    private fun ready() =
        ContentStatus.Ready(
            bundle =
                ContentBundle(
                    festival =
                        Festival(
                            name = "Yadlo",
                            tagline = "Mouille ton corps, arrose ton esprit",
                            website = "https://www.yadlo.ch/",
                            currentEditionId = "2026",
                            minSupportedAppVersion = null,
                            social = emptyList(),
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
                            days = listOf(friday(), saturday(), sunday()),
                            categories = listOf(MUSIQUE),
                            happenings = listOf(dubside().happening),
                            slots = listOf(dubside()),
                            partners = emptyList(),
                            figures = emptyList(),
                        ),
                    announcements = emptyList(),
                ),
            updateRequired = false,
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

    private fun saturday() =
        FestivalDay(
            id = "2026:sat",
            name = "Samedi",
            date = "2026-07-11",
            start = Instant.parse("2026-07-11T12:00:00+02:00"),
            end = Instant.parse("2026-07-12T03:00:00+02:00"),
            provenance = Provenance.CONFIRMED,
        )

    private fun sunday() =
        FestivalDay(
            id = "2026:sun",
            name = "Dimanche",
            date = "2026-07-12",
            start = Instant.parse("2026-07-12T12:00:00+02:00"),
            end = Instant.parse("2026-07-12T22:00:00+02:00"),
            provenance = Provenance.CONFIRMED,
        )

    private fun dubside() =
        Slot(
            id = "2026:dubside-sat",
            happening =
                Happening.Artist(
                    id = "dubside",
                    name = "Dubside",
                    category = MUSIQUE,
                    description = null,
                    images = emptyList(),
                    provenance = Provenance.CONFIRMED,
                    genres = emptyList(),
                    links = emptyList(),
                ),
            day = saturday(),
            start = Instant.parse("2026-07-11T16:00:00+02:00"),
            end = Instant.parse("2026-07-11T18:00:00+02:00"),
            provenance = Provenance.CONFIRMED,
        )

    private companion object {
        val MUSIQUE = Category(id = "musique", name = "Musique", order = 1)

        val A_MONTH_BEFORE = Instant.parse("2026-06-08T12:00:00+02:00")
        val THREE_DAYS_BEFORE = Instant.parse("2026-07-07T12:00:00+02:00")
        val SATURDAY_SMALL_HOURS = Instant.parse("2026-07-11T01:00:00+02:00")
        val SATURDAY_BEFORE_DAWN = Instant.parse("2026-07-11T04:00:00+02:00")
        val SATURDAY_AFTERNOON = Instant.parse("2026-07-11T15:45:00+02:00")
        val SATURDAY_EVENING = Instant.parse("2026-07-11T20:00:00+02:00")
        val A_WEEK_AFTER = Instant.parse("2026-07-19T12:00:00+02:00")

        /** Two, so the assertion does not depend on which side of a single tick the advance lands. */
        const val TWO_TICKS_MILLIS = 120_000L
    }
}
