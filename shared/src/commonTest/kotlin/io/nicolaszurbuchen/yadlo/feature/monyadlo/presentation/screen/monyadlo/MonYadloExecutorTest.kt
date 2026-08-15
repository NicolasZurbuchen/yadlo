package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.monyadlo

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
import io.nicolaszurbuchen.yadlo.common.content.domain.model.Venue
import io.nicolaszurbuchen.yadlo.common.plan.domain.fake.FakePlanRepository
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedItem
import io.nicolaszurbuchen.yadlo.common.plan.domain.model.SavedKind
import io.nicolaszurbuchen.yadlo.feature.monyadlo.domain.usecase.ObserveMonYadloContentUseCase
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
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

/** The fiche's clock, for the same reasons — see HappeningExecutorTest. */
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
class MonYadloExecutorTest {
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
    fun onCreate_beforeAnyBundle_hasNoPlanRatherThanAnEmptyOne() =
        monYadloTest { store, _, _, _ ->
            testDispatcher.scheduler.runCurrent()

            assertNull(store.state.content)
        }

    @Test
    fun onCreate_theBundleArrivesWithNothingSaved_isReadAndEmpty() =
        monYadloTest { store, content, _, _ ->
            content.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            assertTrue(store.state.content?.days.orEmpty().isEmpty())
        }

    @Test
    fun onCreate_theBundleArrivesWithSomethingSaved_narrowsItToWhatWasKept() =
        monYadloTest(saved = listOf(savedSlot("2026:dubside-sat"))) { store, content, _, _ ->
            content.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            assertEquals(listOf("2026:sat"), store.state.content?.days?.map { it.id })
            assertEquals(listOf("Dubside"), store.state.content?.days?.single()?.slots?.map { it.name })
        }

    // endregion

    // region the join

    @Test
    fun heartTappedOnAFiche_arrivesHereThroughTheSameCollector() =
        monYadloTest { store, content, _, plan ->
            content.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()
            assertTrue(store.state.content?.days.orEmpty().isEmpty())

            // No intent, no navigation back: this screen never learns a heart was tapped, it only
            // sees the Plan it was already reading change underneath it.
            plan.toggle(savedSlot("2026:dubside-sat"))
            testDispatcher.scheduler.runCurrent()

            assertEquals(1, store.state.content?.days?.size)
        }

    @Test
    fun refresh_landsWhileTheScreenIsOpen_followsIt() =
        monYadloTest(saved = listOf(savedSlot("2026:dubside-sat"))) { store, content, _, _ ->
            content.emitStatus(ready())
            testDispatcher.scheduler.runCurrent()

            content.emitStatus(ready(artistName = "Dubside b2b"))
            testDispatcher.scheduler.runCurrent()

            assertEquals(listOf("Dubside b2b"), store.state.content?.days?.single()?.slots?.map { it.name })
        }

    // endregion

    // region ticking

    @Test
    fun tick_advancesTheInstantTheRowsPillsAreMeasuredAgainst() =
        monYadloTest { store, _, clock, _ ->
            testDispatcher.scheduler.runCurrent()
            assertEquals(SATURDAY_AFTERNOON, store.state.now)

            clock.instant = SATURDAY_EVENING
            testDispatcher.scheduler.advanceTimeBy(TWO_TICKS_MILLIS)
            testDispatcher.scheduler.runCurrent()

            assertEquals(SATURDAY_EVENING, store.state.now)
        }

    @Test
    fun clockJumps_picksItUpWithoutWaitingOutTheTick() =
        monYadloTest { store, _, clock, _ ->
            testDispatcher.scheduler.runCurrent()

            clock.jumpTo(SATURDAY_EVENING)
            testDispatcher.scheduler.runCurrent()

            assertEquals(SATURDAY_EVENING, store.state.now)
        }

    // endregion

    /** The ticker never stops on its own, so every store is disposed even when an assertion throws. */
    private fun monYadloTest(
        saved: List<SavedItem> = emptyList(),
        block: suspend TestScope.(MonYadloStore, FakeContentRepository, SettableClock, FakePlanRepository) -> Unit,
    ) = runTest {
        val contentRepository = FakeContentRepository()
        val planRepository = FakePlanRepository().apply { emitSaved(saved) }
        val clock = SettableClock(SATURDAY_AFTERNOON)
        val store =
            MonYadloStoreFactory(
                storeFactory = DefaultStoreFactory(),
                observeMonYadloContent = ObserveMonYadloContentUseCase(contentRepository, planRepository),
                clock = clock,
            ).create()

        try {
            block(store, contentRepository, clock, planRepository)
        } finally {
            store.dispose()
        }
    }

    private fun savedSlot(id: String) = SavedItem(id = id, kind = SavedKind.SLOT, editionId = "2026")

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

        return ContentStatus.Ready(
            bundle =
                ContentBundle(
                    festival =
                        Festival(
                            name = "Yadlo",
                            tagline = "Mouille ton corps, arrose ton esprit",
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
                            happenings = listOf(dubside),
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
