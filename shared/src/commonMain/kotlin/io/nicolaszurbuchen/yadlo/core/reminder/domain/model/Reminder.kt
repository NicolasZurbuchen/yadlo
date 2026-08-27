package io.nicolaszurbuchen.yadlo.core.reminder.domain.model

import kotlin.time.Instant

/**
 * One thing to fire, at one instant.
 *
 * [id] is unique within a single planning pass and nothing else, because a pass replaces everything
 * that was scheduled rather than diffing against it — see `ReminderScheduler`. It is still built
 * from the Edition-qualified Slot id rather than an index, so the same Slot keeps the same id across
 * passes and a device that reschedules mid-festival does not stack duplicates.
 *
 * [staleAfter] is when the fired notification stops being worth reading — for a Slot, the moment it
 * ends. Android is told once, at schedule time, and dismisses it itself; iOS has no equivalent and
 * sweeps on next launch. Null means it never goes stale on its own.
 */
data class Reminder(
    val id: String,
    val at: Instant,
    val subject: ReminderSubject,
    val staleAfter: Instant?,
)
