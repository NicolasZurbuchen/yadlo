package io.nicolaszurbuchen.yadlo.core.reminder.domain.model

import kotlin.time.Instant

/**
 * What a [Reminder] is about, carrying the facts and none of the prose.
 *
 * The sentence a visitor reads is assembled outside the domain, where the string resources are, so
 * this holds a Happening's name and the instant it starts rather than "Dubside commence dans 30
 * minutes". That is what lets the planner be tested by asserting instants and ids instead of French.
 */
sealed class ReminderSubject {
    /**
     * A saved Slot about to start. [happeningId] is what a tap opens — the fiche, not the Slot,
     * because a Slot has no screen of its own.
     */
    data class SlotStarting(
        val happeningId: String,
        val happeningName: String,
        val startsAt: Instant,
    ) : ReminderSubject()

    data class MilestoneReached(
        val milestone: ReminderMilestone,
    ) : ReminderSubject()
}
