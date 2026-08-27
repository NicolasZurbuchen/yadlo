package io.nicolaszurbuchen.yadlo.core.reminder.domain.usecase

import io.nicolaszurbuchen.yadlo.core.content.domain.model.FestivalDay
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Slot
import io.nicolaszurbuchen.yadlo.core.plan.domain.model.SavedItem
import io.nicolaszurbuchen.yadlo.core.plan.domain.model.SavedKind
import io.nicolaszurbuchen.yadlo.core.reminder.domain.model.Reminder
import io.nicolaszurbuchen.yadlo.core.reminder.domain.model.ReminderMilestone
import io.nicolaszurbuchen.yadlo.core.reminder.domain.model.ReminderSubject
import io.nicolaszurbuchen.yadlo.core.time.APPROACHING_LEAD
import io.nicolaszurbuchen.yadlo.core.time.FESTIVAL_TIME_ZONE
import io.nicolaszurbuchen.yadlo.core.time.liveEnd
import io.nicolaszurbuchen.yadlo.core.time.liveStart
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

/**
 * Everything that should be scheduled right now, given what is saved and what the content says.
 *
 * **It takes `now` rather than injecting a clock, and that is the one thing about this class worth
 * reading twice.** Every other time-dependent use case here injects [kotlin.time.Clock] and gets the
 * one the debug panel can move, which is what makes a live pill checkable in August. A reminder must
 * not read that clock — the OS scheduler it ends up in compares against wall time — but the choice
 * of which clock is the caller's to make and stating it as a parameter is what keeps that choice
 * visible. It also leaves this a pure function: a test sits at 23:45 on the Friday by passing an
 * instant, with no clock at all.
 *
 * The output is the complete desired state, not a delta. The caller replaces rather than reconciles,
 * so anything absent here is cancelled by omission — an unhearted Slot, a Slot the content dropped,
 * a Slot whose reminder instant has passed. That is what makes every one of those cases the same
 * case.
 */
class PlanRemindersUseCase {
    operator fun invoke(
        saved: List<SavedItem>,
        slots: List<Slot>,
        days: List<FestivalDay>,
        hasPublishedProgramme: Boolean,
        now: Instant,
    ): List<Reminder> {
        val milestones = milestones(days, hasPublishedProgramme, now)

        // Milestones are budgeted first because there are at most three of them and they are the
        // only reminders somebody with an empty Plan can receive. Slots take what is left, nearest
        // first — see MAX_SCHEDULED for what the budget is protecting.
        val slotReminders =
            slotReminders(saved, slots, now)
                .take(MAX_SCHEDULED - milestones.size)

        return (milestones + slotReminders).sortedBy { it.at }
    }

    private fun slotReminders(
        saved: List<SavedItem>,
        slots: List<Slot>,
        now: Instant,
    ): List<Reminder> {
        val savedSlotIds = saved.filter { it.kind == SavedKind.SLOT }.map { it.id }.toSet()

        return slots
            .filter { it.id in savedSlotIds }
            .map { slot ->
                Reminder(
                    id = "$SLOT_PREFIX${slot.id}",
                    at = slot.start.minus(REMINDER_LEAD),
                    subject =
                        ReminderSubject.SlotStarting(
                            happeningId = slot.happening.id,
                            happeningName = slot.happening.name,
                            startsAt = slot.start,
                        ),
                    staleAfter = slot.end,
                )
            }
            .filter { it.at > now }
            .sortedBy { it.at }
    }

    private fun milestones(
        days: List<FestivalDay>,
        hasPublishedProgramme: Boolean,
        now: Instant,
    ): List<Reminder> {
        val liveStart = days.liveStart() ?: return emptyList()
        val liveEnd = days.liveEnd() ?: return emptyList()

        // The same gate DerivePhaseUseCase applies, for the same reason: a week-out reminder points
        // at a Plan there is nothing to fill in yet.
        val approaching =
            liveStart.minus(APPROACHING_LEAD).takeIf { hasPublishedProgramme }

        val live =
            liveStart
                .toLocalDateTime(FESTIVAL_TIME_ZONE)
                .date
                .atTime(LIVE_ANNOUNCEMENT)
                .toInstant(FESTIVAL_TIME_ZONE)

        return listOfNotNull(
            approaching?.let { ReminderMilestone.APPROACHING to it },
            ReminderMilestone.LIVE to live,
            ReminderMilestone.ENDED to liveEnd,
        )
            .filter { (_, at) -> at > now }
            .map { (milestone, at) ->
                Reminder(
                    id = "$MILESTONE_PREFIX${milestone.name}",
                    at = at,
                    subject = ReminderSubject.MilestoneReached(milestone),
                    staleAfter = null,
                )
            }
    }

    private companion object {
        /**
         * How long before a Slot starts its reminder fires.
         *
         * Thirty rather than sixty because the site is one beach you can cross in four minutes: the
         * reminder is not travel time, it is not losing track of something while you are at the bar.
         *
         * It is also what makes the permissive Android alarm API sufficient. An inexact alarm can
         * drift a few minutes, and a lead this long absorbs that invisibly — which is why nothing
         * here asks for SCHEDULE_EXACT_ALARM. Shortening it would quietly change that.
         */
        val REMINDER_LEAD = 30.minutes

        /**
         * Late enough to be a reasonable hour, early enough to be worth acting on.
         *
         * The LIVE Phase itself begins at midnight, and firing there would be a notification the
         * night before the festival telling somebody it is today.
         */
        val LIVE_ANNOUNCEMENT = LocalTime(hour = 10, minute = 0)

        /**
         * iOS drops local notification requests past 64 per app — silently, with no error and no
         * log — so the cap is a hard platform limit rather than a taste. Sixty leaves headroom
         * without being close enough to matter: the 2026 Edition has 48 Slots in total, so even
         * hearting every single one fits, and the cap only ever bites on an Edition twice the size.
         */
        const val MAX_SCHEDULED = 60

        const val SLOT_PREFIX = "slot:"
        const val MILESTONE_PREFIX = "milestone:"
    }
}
