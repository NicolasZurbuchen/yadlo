package io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

/**
 * Where a Slot is against the clock — DECISIONS.md § One live state for every Slot.
 *
 * A seven-hour open activity and a two-hour DJ set read identically and warn the same way as they
 * end. Written in words on the row and never expressed as layout, which is the whole reason layout
 * B2 won: state that is text and colour survives large type, screen readers and any sort order.
 *
 * Presentation rather than domain, unlike the Phase / PhaseUiModel pair behind Accueil: deriving it
 * needs no port and no injected clock, only the reading of `now` the Store has already put in its
 * State. A UseCase here would buy a domain type the presentation layer is not allowed to import,
 * and therefore a conversion whose only job is to undo itself.
 *
 * It lives in its own file because Mon Yadlo uses the same vocabulary on the same kind of row. It
 * moves up a layer the day that screen exists — not before, per CLAUDE.md on premature sharing.
 */
sealed interface SlotLiveStateUiModel {
    /** Far enough out that the start time already says everything a countdown would. */
    data object Upcoming : SlotLiveStateUiModel

    data class StartingSoon(
        val startsIn: Duration,
    ) : SlotLiveStateUiModel

    /** [progress] is 0f at the downbeat and 1f at the end — the fill on the raised bar. */
    data class Running(
        val progress: Float,
    ) : SlotLiveStateUiModel

    data class Ending(
        val endsIn: Duration,
        val progress: Float,
    ) : SlotLiveStateUiModel

    data object Over : SlotLiveStateUiModel
}

/**
 * The state of a Slot running from [start] to [end], read at [now].
 *
 * Every boundary is inclusive of the state it moves *into*, so a Slot at exactly its end instant is
 * [SlotLiveStateUiModel.Over] rather than briefly still running.
 */
fun slotLiveStateAt(
    now: Instant,
    start: Instant,
    end: Instant,
): SlotLiveStateUiModel {
    val startsIn = start - now
    val endsIn = end - now
    val length = end - start
    // A zero-length Slot is not something the content validator allows, but dividing by it here
    // would be a crash rather than a wrong pixel, and the honest answer for one is "done".
    val progress = if (length.isPositive()) ((now - start) / length).toFloat().coerceIn(0f, 1f) else 1f

    return when {
        !endsIn.isPositive() -> SlotLiveStateUiModel.Over
        startsIn > COUNTDOWN_WINDOW -> SlotLiveStateUiModel.Upcoming
        startsIn.isPositive() -> SlotLiveStateUiModel.StartingSoon(startsIn)
        endsIn <= ENDING_WINDOW -> SlotLiveStateUiModel.Ending(endsIn = endsIn, progress = progress)
        else -> SlotLiveStateUiModel.Running(progress)
    }
}

/**
 * Four hours — DECISIONS.md § Countdowns only inside a four-hour window. Beyond it the day header
 * and the start time already say everything, and "dans 26h" is noise.
 *
 * The published prototype counts down from thirty minutes instead. That is the narrower of the two
 * and the prose is the later decision, so the window is the prose's; what the prototype settled is
 * the layout, which is unchanged either way.
 */
private val COUNTDOWN_WINDOW = 4.hours

/**
 * Twenty minutes, from the prototype. Long enough to be worth walking for, short enough that it is
 * not the state half a one-hour set spends its life in.
 */
private val ENDING_WINDOW = 20.minutes
