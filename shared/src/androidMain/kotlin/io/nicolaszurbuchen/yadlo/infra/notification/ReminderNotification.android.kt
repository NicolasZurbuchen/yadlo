package io.nicolaszurbuchen.yadlo.infra.notification

import android.content.Intent

/**
 * The vocabulary the scheduler and the receiver both have to agree on, in one place because they are
 * the two halves of one crossing: [Notifier] writes these extras into a `PendingIntent` that may sit
 * in the system for weeks, and [ReminderReceiver] reads them back in a process that was very likely
 * dead when they were written.
 */
internal const val REMINDER_CHANNEL_ID = "yadlo.reminders"

/**
 * Groups every reminder under one key so that four sets starting at 22:00 arrive as one stack rather
 * than four separate rows. Android builds the summary itself from API 24 up, which is this app's
 * minimum, so nothing here has to post one.
 */
internal const val REMINDER_GROUP_KEY = "yadlo.reminders"

internal const val EXTRA_ID = "yadlo.notification.id"
internal const val EXTRA_TITLE = "yadlo.notification.title"
internal const val EXTRA_BODY = "yadlo.notification.body"
internal const val EXTRA_TARGET = "yadlo.notification.target"
internal const val EXTRA_TIMEOUT_MILLIS = "yadlo.notification.timeout"

/**
 * A per-reminder scheme, so two `PendingIntent`s for two different reminders are never considered
 * equivalent. Extras are ignored when the system compares them; the data URI is not.
 */
internal fun reminderUri(id: String) = "yadlo-reminder://$id"

/**
 * The [NotificationTarget] a launch intent is carrying, if it was started by a notification tap.
 *
 * Public where the extra key beside it is not, because the application module has to read this and
 * has no business knowing how it is spelled. Null for an ordinary launch from the home screen, which
 * is almost every launch.
 */
fun Intent.notificationTarget(): NotificationTarget? = getStringExtra(EXTRA_TARGET)?.let(NotificationTarget::decode)
