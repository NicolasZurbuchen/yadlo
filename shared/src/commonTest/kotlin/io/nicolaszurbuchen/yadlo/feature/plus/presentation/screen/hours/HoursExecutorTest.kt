package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours

import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.nicolaszurbuchen.yadlo.common.content.domain.fake.FakeContentRepository
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveOpeningDaysUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.day
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ready
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class HoursExecutorTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onCreate_beforeAnyBundle_hasNoDays() =
        runTest {
            val store = createStore(FakeContentRepository())
            testDispatcher.scheduler.runCurrent()

            assertNull(store.state.days)
            store.dispose()
        }

    @Test
    fun onCreate_theBundleArrives_deducesTheHoursFromTheProgramme() =
        runTest {
            val friday = FRIDAY
            val repository = FakeContentRepository()
            val store = createStore(repository)

            repository.emitStatus(
                ready(
                    days = listOf(friday),
                    slots = listOf(slot("2026:a", friday, "2026-07-10T17:00:00+02:00", "2026-07-11T01:30:00+02:00")),
                ),
            )
            testDispatcher.scheduler.runCurrent()

            // No content field was added for this screen. The window is the FestivalDay's own and
            // the programme line comes off its Slots.
            val day = store.state.days?.single()
            assertEquals(Instant.parse("2026-07-10T16:00:00+02:00"), day?.opensAt)
            assertEquals(Instant.parse("2026-07-11T01:30:00+02:00"), day?.lastEndsAt)
            store.dispose()
        }

    @Test
    fun refresh_movesAStage_andTheHoursFollowWithoutAnyoneEditingATime() =
        runTest {
            val friday = FRIDAY
            val repository = FakeContentRepository()
            val store = createStore(repository)
            repository.emitStatus(
                ready(
                    days = listOf(friday),
                    slots = listOf(slot("2026:a", friday, "2026-07-10T17:00:00+02:00", "2026-07-10T23:00:00+02:00")),
                ),
            )
            testDispatcher.scheduler.runCurrent()
            assertEquals(Instant.parse("2026-07-10T23:00:00+02:00"), store.state.days?.single()?.lastEndsAt)

            repository.emitStatus(
                ready(
                    days = listOf(friday),
                    slots =
                        listOf(
                            slot("2026:a", friday, "2026-07-10T17:00:00+02:00", "2026-07-10T23:00:00+02:00"),
                            slot("2026:b", friday, "2026-07-10T23:00:00+02:00", "2026-07-11T01:00:00+02:00"),
                        ),
                ),
            )
            testDispatcher.scheduler.runCurrent()

            // This is the payoff of deducing rather than authoring: a set added during the festival
            // moves the closing time on its own.
            assertEquals(Instant.parse("2026-07-11T01:00:00+02:00"), store.state.days?.single()?.lastEndsAt)
            store.dispose()
        }

    private fun createStore(repository: FakeContentRepository): HoursStore =
        HoursStoreFactory(
            storeFactory = DefaultStoreFactory(),
            observeOpeningDays = ObserveOpeningDaysUseCase(repository),
        ).create()

    private companion object {
        val FRIDAY =
            day(
                id = "2026:fri",
                name = "Vendredi",
                start = "2026-07-10T16:00:00+02:00",
                end = "2026-07-11T02:00:00+02:00",
            )
    }
}
