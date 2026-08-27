package io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel

import kotlin.time.Duration
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
 * It sits in `common/content` because the Programme's rows and a fiche's date rows ask the same
 * question of the same Slot, and a visitor who taps `en cours` on the list must not land on a screen
 * that has gone quiet about it. It moved up here when the fiche arrived — the second caller, not the
 * anticipated one. Mon Yadlo will be the third.
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
 * One hour — DECISIONS.md § Countdowns only inside a one-hour window.
 *
 * It was four, which put `dans 4h` on a row whose start time is written directly underneath it. A
 * countdown in hours says nothing the clock does not, and it says it in a pill — the loudest thing
 * the row has — so most of the Saturday afternoon was shouting a fact nobody needed. Inside an hour
 * the number changes what you do next: it is the difference between finishing your drink and
 * leaving now.
 *
 * The published prototype counts down from thirty minutes, which is narrower still. An hour is the
 * point where "should I start walking" becomes a real question on a site you cross in ten minutes.
 */
private val COUNTDOWN_WINDOW = 60.minutes

/**
 * Twenty minutes, from the prototype. Long enough to be worth walking for, short enough that it is
 * not the state half a one-hour set spends its life in.
 */
private val ENDING_WINDOW = 20.minutes
