package io.nicolaszurbuchen.yadlo.app.notification

import io.nicolaszurbuchen.yadlo.common.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.common.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.common.plan.domain.repository.PlanRepository
import io.nicolaszurbuchen.yadlo.common.reminder.domain.model.Reminder
import io.nicolaszurbuchen.yadlo.common.reminder.domain.model.ReminderMilestone
import io.nicolaszurbuchen.yadlo.common.reminder.domain.model.ReminderSubject
import io.nicolaszurbuchen.yadlo.common.reminder.domain.usecase.PlanRemindersUseCase
import io.nicolaszurbuchen.yadlo.common.time.FESTIVAL_TIME_ZONE
import io.nicolaszurbuchen.yadlo.infra.platform.NotificationTarget
import io.nicolaszurbuchen.yadlo.infra.platform.Notifier
import io.nicolaszurbuchen.yadlo.infra.platform.ScheduledNotification
import io.nicolaszurbuchen.yadlo.infra.time.WallClock
import io.nicolaszurbuchen.yadlo.infra.ui.formatAsTimeOfDay
import kotlinx.coroutines.flow.first
import org.jetbrains.compose.resources.getString
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.notification_approaching_body
import yadlo.shared.generated.resources.notification_approaching_title
import yadlo.shared.generated.resources.notification_ended_body
import yadlo.shared.generated.resources.notification_ended_title
import yadlo.shared.generated.resources.notification_live_body
import yadlo.shared.generated.resources.notification_live_title
import yadlo.shared.generated.resources.notification_slot_body

/**
 * Puts what the planner decided in front of the operating system.
 *
 * It sits in `app/` rather than beside the planner because it is the seam where three things that do
 * not belong in a domain layer meet: the platform scheduler, the string resources, and the wall clock.
 * The planner stays a pure function of instants and ids; this turns its answer into French and hands
 * it over.
 *
 * **Called on every start and every resume, and that is the whole reconciliation strategy.** A pass
 * replaces everything, so an unhearted Slot, a Slot the content dropped, a set whose hours moved and
 * a reminder whose moment has passed are not four cases — they are one, and it is "absent from the
 * list this time". The cost is rescheduling a few dozen alarms on resume, which is not measurable;
 * what it buys is that there is no state to get wrong.
 *
 * **On testing this by hand.** Time travel does not reach it: [WallClock] is deliberately not the
 * clock the debug panel moves, so simulating the Saturday evening changes every screen and no alarm.
 * The way to see a real reminder is to move the *device* clock to just before a Slot — say 21:25 on
 * the Friday — and then open the app, which makes both clocks agree and leaves the alarm genuinely
 * five minutes out. Do it after the bundle has cached: a wrong device clock breaks TLS, and the
 * failure to debug should not be the content fetch.
 */
class ReminderScheduler(
    private val planRepository: PlanRepository,
    private val contentRepository: ContentRepository,
    private val planReminders: PlanRemindersUseCase,
    private val notifier: Notifier,
    private val wallClock: WallClock,
) {
    suspend fun sync() {
        // Scheduling into a permission that was never granted, or was revoked in settings since,
        // would be work that silently does nothing. Clearing is still worth doing: the visitor may
        // have turned notifications off precisely to stop the ones already scheduled.
        if (!notifier.isPermissionGranted()) {
            notifier.replaceScheduled(emptyList())
            return
        }

        val ready = contentRepository.observeStatus().value as? ContentStatus.Ready ?: return
        val edition = ready.bundle.edition

        val reminders =
            planReminders(
                saved = planRepository.observeSaved().first(),
                slots = edition.slots,
                days = edition.days,
                hasPublishedProgramme = edition.slots.isNotEmpty(),
                now = wallClock.now(),
            )

        notifier.replaceScheduled(reminders.map { it.toScheduledNotification() })
    }

    private suspend fun Reminder.toScheduledNotification(): ScheduledNotification =
        when (val subject = subject) {
            is ReminderSubject.SlotStarting -> {
                ScheduledNotification(
                    id = id,
                    at = at,
                    title = subject.happeningName,
                    // The time it starts, never "dans 30 minutes". The alarm is inexact by design —
                    // see PlanRemindersUseCase.REMINDER_LEAD — so a countdown baked in at schedule
                    // time would be a promise the scheduler is not making. A clock time is true
                    // whenever it arrives.
                    body = getString(Res.string.notification_slot_body, subject.startsAt.formatAsTimeOfDay(FESTIVAL_TIME_ZONE)),
                    target = NotificationTarget.Happening(subject.happeningId),
                    staleAfter = staleAfter,
                )
            }

            is ReminderSubject.MilestoneReached -> {
                ScheduledNotification(
                    id = id,
                    at = at,
                    title = getString(subject.milestone.title),
                    body = getString(subject.milestone.body),
                    target = subject.milestone.target,
                    staleAfter = staleAfter,
                )
            }
        }
}

private val ReminderMilestone.title
    get() =
        when (this) {
            ReminderMilestone.APPROACHING -> Res.string.notification_approaching_title
            ReminderMilestone.LIVE -> Res.string.notification_live_title
            ReminderMilestone.ENDED -> Res.string.notification_ended_title
        }

private val ReminderMilestone.body
    get() =
        when (this) {
            ReminderMilestone.APPROACHING -> Res.string.notification_approaching_body
            ReminderMilestone.LIVE -> Res.string.notification_live_body
            ReminderMilestone.ENDED -> Res.string.notification_ended_body
        }

/**
 * Where each one lands, and the reasoning is the same every time: open the screen that answers the
 * sentence. A week out that is Accueil, where the countdown and the practical rows are; on the day
 * it is the Programme, because the only useful next question is what is on; afterwards Accueil
 * again, which is the screen that says thank you.
 */
private val ReminderMilestone.target
    get() =
        when (this) {
            ReminderMilestone.APPROACHING -> NotificationTarget.Home
            ReminderMilestone.LIVE -> NotificationTarget.Programme
            ReminderMilestone.ENDED -> NotificationTarget.Home
        }
