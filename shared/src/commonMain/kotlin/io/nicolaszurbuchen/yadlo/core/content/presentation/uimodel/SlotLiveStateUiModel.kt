package io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel

import io.nicolaszurbuchen.yadlo.infra.text.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.slot_state_ending
import yadlo.shared.generated.resources.slot_state_over
import yadlo.shared.generated.resources.slot_state_running
import yadlo.shared.generated.resources.slot_state_starts_in_minutes
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
 * It sits in `core/content` because the Programme's rows and a fiche's date rows ask the same
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
 * The state in words, or nothing at all.
 *
 * [SlotLiveStateUiModel.Upcoming] is deliberately silent: the start time is written on the row
 * already, and a pill repeating *à venir* would be the loudest thing there saying the least.
 *
 * It sits beside the type rather than in a UiMapper because three of them need it — the Programme's
 * rows, a fiche's date rows and Mon Yadlo's timeline — and a `*UiMapper` may hold nothing but its
 * own State-to-UiModel function. The same argument put [loudestState] beside
 * [SlotSegmentUiModel]. Written out three times before this, and the wording had already drifted:
 * only one copy carried the reasoning below.
 */
fun SlotLiveStateUiModel.stateLabel(): UiText? =
    when (this) {
        SlotLiveStateUiModel.Upcoming -> {
            null
        }

        is SlotLiveStateUiModel.StartingSoon -> {
            // Always minutes, because the window is an hour: an hours branch could only ever fire on
            // the single instant the window opens. Never "dans 0 min" either — under a minute out it
            // still has not started, and one is the smallest true thing to say.
            UiText.Resource(
                Res.string.slot_state_starts_in_minutes,
                listOf(startsIn.inWholeMinutes.coerceAtLeast(1).toString()),
            )
        }

        is SlotLiveStateUiModel.Running -> {
            UiText.Resource(Res.string.slot_state_running)
        }

        // Rounded up the same way and for the same reason: a set with forty seconds left is ending
        // in a minute, not in none.
        is SlotLiveStateUiModel.Ending -> {
            UiText.Resource(
                Res.string.slot_state_ending,
                listOf(endsIn.inWholeMinutes.coerceAtLeast(1).toString()),
            )
        }

        SlotLiveStateUiModel.Over -> {
            UiText.Resource(Res.string.slot_state_over)
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
