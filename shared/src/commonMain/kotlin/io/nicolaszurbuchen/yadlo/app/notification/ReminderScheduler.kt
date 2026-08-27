package io.nicolaszurbuchen.yadlo.app.notification

import io.nicolaszurbuchen.yadlo.core.content.domain.model.ContentStatus
import io.nicolaszurbuchen.yadlo.core.content.domain.repository.ContentRepository
import io.nicolaszurbuchen.yadlo.core.plan.domain.repository.PlanRepository
import io.nicolaszurbuchen.yadlo.core.reminder.domain.model.Reminder
import io.nicolaszurbuchen.yadlo.core.reminder.domain.model.ReminderMilestone
import io.nicolaszurbuchen.yadlo.core.reminder.domain.model.ReminderSubject
import io.nicolaszurbuchen.yadlo.core.reminder.domain.repository.ReminderSettingsRepository
import io.nicolaszurbuchen.yadlo.core.reminder.domain.usecase.PlanRemindersUseCase
import io.nicolaszurbuchen.yadlo.core.time.FESTIVAL_TIME_ZONE
import io.nicolaszurbuchen.yadlo.infra.notification.NotificationTarget
import io.nicolaszurbuchen.yadlo.infra.notification.Notifier
import io.nicolaszurbuchen.yadlo.infra.notification.ScheduledNotification
import io.nicolaszurbuchen.yadlo.infra.time.WallClock
import io.nicolaszurbuchen.yadlo.infra.ui.formatAsTimeOfDay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
 * list this time". What it buys is that there is no state to get wrong.
 *
 * **[lastScheduled] is what stops that costing something on every resume.** Replacing is cheap to
 * reason about and not cheap to perform: on Android each id is a `PendingIntent` lookup, a cancel and
 * a second lookup before the alarm is even set, so a Plan with forty Slots is a couple of hundred
 * binder calls to `AlarmManager` — every time the visitor flips back to the app. Nothing in the
 * desired list is relative to now, so an unchanged Plan and unchanged content produce a list that is
 * equal in the data-class sense, and there is nothing to say to the system.
 *
 * It is held in memory rather than persisted, which is deliberate: after process death nothing here
 * can know what the OS still holds, so the first pass of a new process always talks to it.
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
    private val settingsRepository: ReminderSettingsRepository,
    private val planReminders: PlanRemindersUseCase,
    private val notifier: Notifier,
    private val wallClock: WallClock,
) {
    /**
     * Two callers can arrive at once — a heart tapped as the app resumes — and a pass is a
     * cancel-everything followed by a schedule-everything. Interleaved, two of those can leave
     * [lastScheduled] describing a set the system does not actually hold, which is worse than the
     * work the lock costs.
     */
    private val mutex = Mutex()

    private var lastScheduled: List<ScheduledNotification>? = null

    suspend fun sync() {
        mutex.withLock {
            // Before the short-circuit below, never after it: what is delivered goes stale on the
            // festival's clock, not on whether the Plan changed, and the ordinary resume is exactly
            // the case where the schedule is identical and the shade is not.
            notifier.clearStaleDelivered()

            val desired = desiredNotifications() ?: return@withLock

            if (desired == lastScheduled) return@withLock

            notifier.replaceScheduled(desired)
            lastScheduled = desired
        }
    }

    /**
     * What should be scheduled right now, or null for "no answer" — which is not the same as an
     * empty list and must not be treated as one. Content that has not loaded yet cannot say what a
     * visitor's reminders are, and wiping the alarms of a cold start to find out a moment later is
     * the one outcome worth avoiding here.
     */
    private suspend fun desiredNotifications(): List<ScheduledNotification>? {
        // Two switches, checked in the order they belong to their owners. The visitor's own answer
        // comes first — Plus > Notifications, off by their choice — and the operating system's
        // second. Either one closed means an empty list rather than no list: clearing is the point,
        // since somebody who has just turned the switch off is asking for the reminders already
        // scheduled to stop, not merely for no new ones.
        if (!settingsRepository.observeEnabled().first()) return emptyList()

        if (!notifier.isPermissionGranted()) return emptyList()

        val ready = contentRepository.observeStatus().value as? ContentStatus.Ready ?: return null
        val edition = ready.bundle.edition

        val reminders =
            planReminders(
                saved = planRepository.observeSaved().first(),
                slots = edition.slots,
                days = edition.days,
                hasPublishedProgramme = edition.slots.isNotEmpty(),
                now = wallClock.now(),
            )

        return reminders.map { it.toScheduledNotification() }
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
