package io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel

import io.nicolaszurbuchen.yadlo.infra.text.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.slot_state_ending
import yadlo.shared.generated.resources.slot_state_over
import yadlo.shared.generated.resources.slot_state_running
import yadlo.shared.generated.resources.slot_state_starts_in_minutes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

/**
 * The most clock-sensitive function in the app, and until now the least tested one.
 *
 * Everything the injected clock exists for runs through here — the pills on the Programme, on a
 * fiche and on Mon Yadlo — and the festival is eleven months out, so none of it can be checked by
 * opening the app. The two windows below have already been changed once (the countdown was four
 * hours), and nothing would have caught changing them back.
 */
class SlotLiveStateUiModelTest {
    // region slotLiveStateAt

    @Test
    fun slotLiveStateAt_farFromTheStart_isUpcomingAndSaysNothing() {
        val state = stateAt(startsIn = 3.hours)

        assertEquals(SlotLiveStateUiModel.Upcoming, state)
        assertNull(state.stateLabel())
    }

    @Test
    fun slotLiveStateAt_theCountdownWindowIsOneHour() {
        // The boundary itself, from both sides. This is the assertion that pins the window: it was
        // four hours once, and putting it back would break here and nowhere else.
        assertEquals(SlotLiveStateUiModel.Upcoming, stateAt(startsIn = 61.minutes))
        assertIs<SlotLiveStateUiModel.StartingSoon>(stateAt(startsIn = 60.minutes))
        assertIs<SlotLiveStateUiModel.StartingSoon>(stateAt(startsIn = 1.minutes))
    }

    @Test
    fun slotLiveStateAt_startingSoonCarriesTheTimeLeft() {
        val state = assertIs<SlotLiveStateUiModel.StartingSoon>(stateAt(startsIn = 45.minutes))

        assertEquals(45.minutes, state.startsIn)
    }

    @Test
    fun slotLiveStateAt_atTheDownbeatItIsRunning() {
        // Inclusive of the state it moves *into*: at exactly the start instant the Slot has begun.
        val state = assertIs<SlotLiveStateUiModel.Running>(stateAt(startsIn = 0.minutes))

        assertEquals(0f, state.progress)
    }

    @Test
    fun slotLiveStateAt_progressIsTheFractionElapsed() {
        val start = NOW - 30.minutes
        val end = NOW + 30.minutes

        val state = assertIs<SlotLiveStateUiModel.Running>(slotLiveStateAt(now = NOW, start = start, end = end))

        assertEquals(0.5f, state.progress)
    }

    @Test
    fun slotLiveStateAt_theEndingWindowIsTwentyMinutes() {
        // A two-hour Slot, so the ending window is well inside it and the boundary is the only
        // thing under test. Started 100 minutes ago leaves exactly 20 to run, and the boundary is
        // inclusive of the state it moves into — so that instant is already Ending, not the last
        // moment of Running.
        assertIs<SlotLiveStateUiModel.Ending>(stateAt(startsIn = -(100.minutes), length = 2.hours))
        assertIs<SlotLiveStateUiModel.Running>(stateAt(startsIn = -(100.minutes) + 1.seconds, length = 2.hours))
    }

    @Test
    fun slotLiveStateAt_endingCarriesBothTheTimeLeftAndTheProgress() {
        val state =
            assertIs<SlotLiveStateUiModel.Ending>(
                stateAt(startsIn = -(50.minutes), length = 1.hours),
            )

        assertEquals(10.minutes, state.endsIn)
        assertEquals(50f / 60f, state.progress)
    }

    @Test
    fun slotLiveStateAt_atExactlyItsEndItIsOverRatherThanBrieflyRunning() {
        // The rule the KDoc states, and the one a naive `endsIn >= 0` would get wrong.
        assertEquals(SlotLiveStateUiModel.Over, stateAt(startsIn = -(1.hours), length = 1.hours))
        assertIs<SlotLiveStateUiModel.Ending>(stateAt(startsIn = -(1.hours) + 1.seconds, length = 1.hours))
    }

    @Test
    fun slotLiveStateAt_longPastItIsOver() {
        assertEquals(SlotLiveStateUiModel.Over, stateAt(startsIn = -(2.hours), length = 1.hours))
    }

    @Test
    fun slotLiveStateAt_aZeroLengthSlotIsDoneRatherThanACrash() {
        // Not something the content validator allows, but dividing by it would be a crash rather
        // than a wrong pixel.
        assertEquals(SlotLiveStateUiModel.Over, slotLiveStateAt(now = NOW, start = NOW, end = NOW))
    }

    @Test
    fun slotLiveStateAt_readBeforeItStartsProgressNeverGoesNegative() {
        // `coerceIn` earns its place here: `now` is before `start`, so the raw fraction is negative
        // and would draw a bar running backwards off its left edge.
        val state = assertIs<SlotLiveStateUiModel.StartingSoon>(stateAt(startsIn = 30.minutes))

        assertEquals(30.minutes, state.startsIn)
    }

    // endregion

    // region stateLabel

    @Test
    fun stateLabel_upcomingSaysNothing() {
        assertNull(SlotLiveStateUiModel.Upcoming.stateLabel())
    }

    @Test
    fun stateLabel_countsDownInWholeMinutes() {
        assertEquals(
            UiText.Resource(Res.string.slot_state_starts_in_minutes, listOf("45")),
            SlotLiveStateUiModel.StartingSoon(startsIn = 45.minutes).stateLabel(),
        )
    }

    @Test
    fun stateLabel_underAMinuteOutItStillSaysOneRatherThanZero() {
        // "dans 0 min" would be wrong twice: it has not started, and zero is not a countdown.
        assertEquals(
            UiText.Resource(Res.string.slot_state_starts_in_minutes, listOf("1")),
            SlotLiveStateUiModel.StartingSoon(startsIn = 20.seconds).stateLabel(),
        )
    }

    @Test
    fun stateLabel_runningTakesNoArgument() {
        assertEquals(
            UiText.Resource(Res.string.slot_state_running),
            SlotLiveStateUiModel.Running(progress = 0.4f).stateLabel(),
        )
    }

    @Test
    fun stateLabel_endingCountsDownAndRoundsUpTheSameWay() {
        assertEquals(
            UiText.Resource(Res.string.slot_state_ending, listOf("15")),
            SlotLiveStateUiModel.Ending(endsIn = 15.minutes, progress = 0.9f).stateLabel(),
        )
        assertEquals(
            UiText.Resource(Res.string.slot_state_ending, listOf("1")),
            SlotLiveStateUiModel.Ending(endsIn = 40.seconds, progress = 0.99f).stateLabel(),
        )
    }

    @Test
    fun stateLabel_overTakesNoArgument() {
        assertEquals(
            UiText.Resource(Res.string.slot_state_over),
            SlotLiveStateUiModel.Over.stateLabel(),
        )
    }

    // endregion

    private fun stateAt(
        startsIn: kotlin.time.Duration,
        length: kotlin.time.Duration = 1.hours,
    ): SlotLiveStateUiModel {
        val start = NOW + startsIn

        return slotLiveStateAt(now = NOW, start = start, end = start + length)
    }

    private companion object {
        val NOW = Instant.parse("2026-07-11T15:00:00+02:00")
    }
}
