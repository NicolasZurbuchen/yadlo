package io.nicolaszurbuchen.yadlo.common.reminder.domain.model

/**
 * A moment in the festival's own year worth telling somebody about, whatever they have saved.
 *
 * **Three, not five.** The two Phases missing from this list are missing for different reasons and
 * neither is an oversight: OFF_SEASON is the absence of a festival rather than an event in one, and
 * ANNOUNCED is the moment a dormant app would most like to speak and precisely the one it cannot
 * reach — the phone has to be told the dates exist, and nothing local can learn them while the app
 * is closed. That needs push, which SPEC.md defers past v1.
 *
 * The three that remain are all reachable because they are derivable: once an Edition's days are in
 * the bundle, every one of these instants is arithmetic.
 */
enum class ReminderMilestone {
    /** A week out. The Plan is worth filling in now, and the practical screens have answers. */
    APPROACHING,

    /**
     * The first day — deliberately *not* at the Phase boundary, which is midnight. A phone buzzing
     * at 00:00 to say the festival is today wakes somebody the night before it starts.
     */
    LIVE,

    /** The morning after the last day, which is also when the Phase itself hands over. */
    ENDED,
}
