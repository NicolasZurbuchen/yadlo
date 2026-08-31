package io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.minutes

/**
 * A row is a Happening on a day and can hold several Slots — SUP Yoga runs three separate hours on
 * the Saturday — so which of them the row speaks for is a real decision with a documented tie-break,
 * and it had no test.
 */
class SlotSegmentUiModelTest {
    @Test
    fun loudestState_runningBeatsEverything() {
        // A row with something happening on it right now has exactly one useful thing to say.
        val row =
            listOf(
                segment(SlotLiveStateUiModel.Over),
                segment(SlotLiveStateUiModel.Running(progress = 0.3f)),
                segment(SlotLiveStateUiModel.StartingSoon(startsIn = 10.minutes)),
            )

        assertIs<SlotLiveStateUiModel.Running>(row.loudestState())
    }

    @Test
    fun loudestState_endingRanksWithRunning() {
        val row = listOf(segment(SlotLiveStateUiModel.StartingSoon(startsIn = 5.minutes)), segment(ENDING))

        assertEquals(ENDING, row.loudestState())
    }

    @Test
    fun loudestState_startingSoonBeatsUpcomingAndOver() {
        val row =
            listOf(
                segment(SlotLiveStateUiModel.Over),
                segment(SlotLiveStateUiModel.Upcoming),
                segment(SlotLiveStateUiModel.StartingSoon(startsIn = 30.minutes)),
            )

        assertIs<SlotLiveStateUiModel.StartingSoon>(row.loudestState())
    }

    @Test
    fun loudestState_upcomingOutranksOverSoAFinishedMorningDoesNotHideTheAfternoon() {
        // A row whose 14:00 is over and whose 18:00 has not started is not finished, and saying
        // *Terminé* would hide the rest of the day.
        val row = listOf(segment(SlotLiveStateUiModel.Over), segment(SlotLiveStateUiModel.Upcoming))

        assertEquals(SlotLiveStateUiModel.Upcoming, row.loudestState())
    }

    @Test
    fun loudestState_overOnlyWinsUnopposed() {
        val row = listOf(segment(SlotLiveStateUiModel.Over), segment(SlotLiveStateUiModel.Over))

        assertEquals(SlotLiveStateUiModel.Over, row.loudestState())
    }

    @Test
    fun loudestState_tiesGoToTheNearerSlot() {
        // Ties break on the order the Slots are in, which is chronological — so of two merely
        // upcoming Slots the row carries the nearer one, whose countdown is the one worth reading.
        val nearer = SlotLiveStateUiModel.StartingSoon(startsIn = 10.minutes)
        val later = SlotLiveStateUiModel.StartingSoon(startsIn = 40.minutes)

        assertEquals(nearer, listOf(segment(nearer), segment(later)).loudestState())
    }

    @Test
    fun loudestState_anEmptyRowIsOver() {
        // Not reachable from the content — a row exists because it has Slots — but the fallback is
        // a real branch and *Terminé* is the quietest thing it could say.
        assertEquals(SlotLiveStateUiModel.Over, emptyList<SlotSegmentUiModel>().loudestState())
    }

    private fun segment(state: SlotLiveStateUiModel) =
        SlotSegmentUiModel(
            id = "2026:slot-${state::class.simpleName}-${state.hashCode()}",
            timeText = "16:00 – 17:00",
            state = state,
            barStart = 0f,
            barEnd = 1f,
        )

    private companion object {
        val ENDING = SlotLiveStateUiModel.Ending(endsIn = 5.minutes, progress = 0.9f)
    }
}
