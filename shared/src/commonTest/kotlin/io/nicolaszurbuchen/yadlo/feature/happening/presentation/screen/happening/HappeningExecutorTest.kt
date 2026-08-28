package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening

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
import io.nicolaszurbuchen.yadlo.core.plan.domain.fake.FakePlanRepository
import io.nicolaszurbuchen.yadlo.core.plan.domain.model.SavedItem
import io.nicolaszurbuchen.yadlo.core.plan.domain.model.SavedKind
import io.nicolaszurbuchen.yadlo.core.plan.domain.usecase.ToggleSavedUseCase
import io.nicolaszurbuchen.yadlo.feature.happening.domain.usecase.ObserveHappeningDetailUseCase
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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

/**
 * The Programme's clock, for the same reasons — see ProgrammeExecutorTest. Assigning [instant] moves
 * the reading silently; [jumpTo] also signals, which is what the debug time-travel panel does.
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
class HappeningExecutorTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // region bootstrap

    @Test
    fun onCreate_beforeAnyBundle_isNeitherLoadedNorMissing() =
        happeningTest(happeningId = "dubside") { store, _, _, _ ->
            testDispatcher.scheduler.runCurrent()

            assertFalse(store.state.isLoaded)
            assertNull(store.state.detail)
        }

    @Test
    fun onCreate_theBundleArrives_narrowsItToThisHappeningAlone() =
        happeningTest(happeningId = "dubside") { store, repository, _, _ ->
            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            assertTrue(store.state.isLoaded)
            assertEquals("Dubside", store.state.detail?.name)
            assertEquals(listOf("2026:dubside-sat"), store.state.detail?.slots?.map { it.id })
        }

    @Test
    fun onCreate_anIdTheEditionDoesNotDeclare_loadsWithNoFicheRatherThanSpinningForever() =
        happeningTest(happeningId = "gone-since-last-refresh") { store, repository, _, _ ->
            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            assertTrue(store.state.isLoaded)
            assertNull(store.state.detail)
        }

    @Test
    fun refresh_landsWhileTheFicheIsOpen_followsIt() =
        happeningTest(happeningId = "dubside") { store, repository, _, _ ->
            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            // A fiche is exactly where someone sits long enough for a refresh to land — reading a
            // set's time while the association corrects it.
            repository.emitStatus(ready(artistName = "Dubside b2b"))
            testDispatcher.scheduler.runCurrent()

            assertEquals("Dubside b2b", store.state.detail?.name)
        }

    // endregion

    // region ticking

    @Test
    fun tick_advancesTheInstantTheDateRowsPillsAreMeasuredAgainst() =
        happeningTest(happeningId = "dubside") { store, _, clock, _ ->
            testDispatcher.scheduler.runCurrent()
            assertEquals(SATURDAY_AFTERNOON, store.state.now)

            clock.instant = SATURDAY_EVENING
            testDispatcher.scheduler.advanceTimeBy(TWO_TICKS_MILLIS)
            testDispatcher.scheduler.runCurrent()

            assertEquals(SATURDAY_EVENING, store.state.now)
        }

    @Test
    fun clockJumps_picksItUpWithoutWaitingOutTheTick() =
        happeningTest(happeningId = "dubside") { store, _, clock, _ ->
            testDispatcher.scheduler.runCurrent()

            clock.jumpTo(SATURDAY_EVENING)
            testDispatcher.scheduler.runCurrent()

            assertEquals(SATURDAY_EVENING, store.state.now)
        }

    // endregion

    // region LinkClicked

    @Test
    fun linkClicked_leavesTheAppRatherThanNavigating() =
        happeningTest(happeningId = "dubside") { store, _, _, _ ->
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(HappeningIntent.LinkClicked("https://djalf.ch/"))

                // A link out is not a destination and never joins a back stack, so it is published
                // as a Label for the platform rather than handed to the navigator.
                assertEquals(HappeningLabel.OpenUrl("https://djalf.ch/"), awaitItem())
            }
        }

    // endregion

    // region the hearts

    @Test
    fun slotHeartClicked_savesThatSlotAloneAndStampsItWithTheEdition() =
        happeningTest(happeningId = "dubside") { store, repository, _, plan ->
            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            store.accept(HappeningIntent.SlotHeartClicked("2026:dubside-sat"))
            testDispatcher.scheduler.runCurrent()

            assertEquals(
                listOf(SavedItem(id = "2026:dubside-sat", kind = SavedKind.SLOT, editionId = "2026")),
                plan.toggled,
            )
        }

    @Test
    fun slotHeartClicked_comesBackThroughTheJoinRatherThanBeingAssumed() =
        happeningTest(happeningId = "dubside") { store, repository, _, _ ->
            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()
            assertFalse(store.state.detail?.slots.orEmpty().any { it.planned })

            store.accept(HappeningIntent.SlotHeartClicked("2026:dubside-sat"))
            testDispatcher.scheduler.runCurrent()

            // No Message was dispatched for this: the filled heart is the repository answering.
            assertTrue(store.state.detail?.slots.orEmpty().all { it.planned })
        }

    @Test
    fun slotHeartClicked_twice_leavesTheSlotOffThePlan() =
        happeningTest(happeningId = "dubside") { store, repository, _, _ ->
            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            store.accept(HappeningIntent.SlotHeartClicked("2026:dubside-sat"))
            testDispatcher.scheduler.runCurrent()
            store.accept(HappeningIntent.SlotHeartClicked("2026:dubside-sat"))
            testDispatcher.scheduler.runCurrent()

            assertFalse(store.state.detail?.slots.orEmpty().any { it.planned })
        }

    @Test
    fun wishlistHeartClicked_savesTheStandItselfRatherThanAnyOfItsHours() =
        happeningTest(happeningId = "snack") { store, repository, _, plan ->
            repository.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            store.accept(HappeningIntent.WishlistHeartClicked)
            testDispatcher.scheduler.runCurrent()

            assertEquals(
                listOf(SavedItem(id = "snack", kind = SavedKind.STAND, editionId = "2026")),
                plan.toggled,
            )
            assertEquals(true, store.state.detail?.wishlisted)
        }

    // endregion

    /** The ticker never stops on its own, so every store is disposed even when an assertion throws. */
    private fun happeningTest(
        happeningId: String,
        block: suspend TestScope.(HappeningStore, FakeContentRepository, SettableClock, FakePlanRepository) -> Unit,
    ) = runTest {
        val repository = FakeContentRepository()
        val planRepository = FakePlanRepository()
        val clock = SettableClock(SATURDAY_AFTERNOON)
        val store =
            HappeningStoreFactory(
                storeFactory = DefaultStoreFactory(),
                observeHappeningDetail = ObserveHappeningDetailUseCase(repository, planRepository),
                toggleSaved = ToggleSavedUseCase(planRepository, repository),
                clock = clock,
            ).create(happeningId)

        try {
            block(store, repository, clock, planRepository)
        } finally {
            store.dispose()
        }
    }

    private fun ready(artistName: String = "Dubside"): ContentStatus.Ready {
        val dubside =
            Happening.Artist(
                id = "dubside",
                name = artistName,
                category = MUSIQUE,
                description = null,
                images = emptyList(),
                provenance = Provenance.CONFIRMED,
                genres = emptyList(),
                links = emptyList(),
            )

        val snack =
            Happening.Stand(
                id = "snack",
                name = "Le Snack",
                category = MUSIQUE,
                description = null,
                images = emptyList(),
                provenance = Provenance.CONFIRMED,
                offering = "Frites",
                links = emptyList(),
                menu = emptyList(),
            )

        return ContentStatus.Ready(
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
                            days = listOf(SATURDAY),
                            categories = listOf(MUSIQUE),
                            happenings = listOf(dubside, snack),
                            slots =
                                listOf(
                                    Slot(
                                        id = "2026:dubside-sat",
                                        happening = dubside,
                                        day = SATURDAY,
                                        start = Instant.parse("2026-07-11T16:00:00+02:00"),
                                        end = Instant.parse("2026-07-11T18:00:00+02:00"),
                                        provenance = Provenance.CONFIRMED,
                                    ),
                                ),
                            partners = emptyList(),
                            figures = emptyList(),
                        ),
                    announcements = emptyList(),
                ),
            updateRequired = false,
        )
    }

    private companion object {
        val MUSIQUE = Category(id = "musique", name = "Musique", order = 1)

        val SATURDAY =
            FestivalDay(
                id = "2026:sat",
                name = "Samedi",
                date = "2026-07-11",
                start = Instant.parse("2026-07-11T12:00:00+02:00"),
                end = Instant.parse("2026-07-12T03:00:00+02:00"),
                provenance = Provenance.CONFIRMED,
            )

        val SATURDAY_AFTERNOON = Instant.parse("2026-07-11T15:45:00+02:00")
        val SATURDAY_EVENING = Instant.parse("2026-07-11T20:30:00+02:00")

        /** Two minute-long ticks, so a test never depends on landing exactly on a boundary. */
        const val TWO_TICKS_MILLIS = 120_000L
    }
}
