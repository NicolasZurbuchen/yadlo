package io.nicolaszurbuchen.yadlo.infra.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.getString
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.notification_channel_description
import yadlo.shared.generated.resources.notification_channel_name

actual class Notifier(
    private val context: Context,
) {
    actual suspend fun isPermissionGranted(): Boolean =
        // Covers both ways a visitor can say no: the runtime POST_NOTIFICATIONS prompt on Android 13
        // and up, and the channel being switched off in system settings afterwards. Neither is worth
        // distinguishing here — both mean nothing this app posts would be seen.
        NotificationManagerCompat.from(context).areNotificationsEnabled()

    actual suspend fun replaceScheduled(notifications: List<ScheduledNotification>) {
        val channelName = getString(Res.string.notification_channel_name)
        val channelDescription = getString(Res.string.notification_channel_description)

        withContext(Dispatchers.IO) {
            ensureChannel(channelName, channelDescription)

            val alarmManager = context.getSystemService(AlarmManager::class.java)

            scheduledIds().forEach { id ->
                pendingAlarm(id, PendingIntent.FLAG_NO_CREATE)?.let { pending ->
                    alarmManager.cancel(pending)
                    pending.cancel()
                }
            }

            notifications.forEach { notification -> schedule(alarmManager, notification) }

            rememberScheduledIds(notifications.map { it.id }.toSet())
        }
    }

    /**
     * Nothing to do. Android is told the timeout when the alarm is scheduled — see
     * [ReminderReceiver] — and dismisses the notification itself when the Slot ends, without the app
     * running or even existing. The seam carries this method for iOS, which has no such thing.
     */
    actual suspend fun clearStaleDelivered() = Unit

    private fun schedule(
        alarmManager: AlarmManager,
        notification: ScheduledNotification,
    ) {
        val intent =
            Intent(context, ReminderReceiver::class.java).apply {
                data = Uri.parse(reminderUri(notification.id))
                putExtra(EXTRA_ID, notification.id)
                putExtra(EXTRA_TITLE, notification.title)
                putExtra(EXTRA_BODY, notification.body)
                putExtra(EXTRA_TARGET, notification.target.encode())
                notification.staleAfter?.let {
                    putExtra(EXTRA_TIMEOUT_MILLIS, (it - notification.at).inWholeMilliseconds)
                }
            }

        val pending =
            PendingIntent.getBroadcast(
                context,
                notification.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        // **Inexact on purpose, and it is the lead time that pays for it.** The exact variants need
        // SCHEDULE_EXACT_ALARM, which Android 14 denies by default and only returns through a system
        // settings screen, or USE_EXACT_ALARM, which Play restricts to alarm clocks and calendars and
        // reviews at submission. This one needs no permission at all and still fires in Doze; what it
        // costs is a few minutes of drift, which is invisible inside a thirty-minute warning. Shorten
        // PlanRemindersUseCase.REMINDER_LEAD far enough and that stops being true.
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            notification.at.toEpochMilliseconds(),
            pending,
        )
    }

    /**
     * Recreates the `PendingIntent` for [id] without creating one, which is the only way to cancel an
     * alarm scheduled by a process that has since died. Equality ignores extras, so the data URI and
     * the request code are what have to match — see [reminderUri].
     */
    private fun pendingAlarm(
        id: String,
        flags: Int,
    ): PendingIntent? =
        PendingIntent.getBroadcast(
            context,
            id.hashCode(),
            Intent(context, ReminderReceiver::class.java).apply { data = Uri.parse(reminderUri(id)) },
            flags or PendingIntent.FLAG_IMMUTABLE,
        )

    /**
     * The channel is created here rather than in [ReminderReceiver] because this is the only path to
     * an alarm existing at all: nothing can fire that was not first scheduled through this method.
     * Creating one that already exists updates its name and leaves everything the visitor changed —
     * importance, sound — alone, which is why it is safe to do on every pass.
     */
    private fun ensureChannel(
        name: String,
        description: String,
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel =
            NotificationChannel(REMINDER_CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
                .apply { this.description = description }

        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    /**
     * What the last pass scheduled.
     *
     * Android cannot be asked which alarms an app holds, so cancelling requires remembering. This is
     * the whole reason a preferences file exists here, and it holds ids and nothing else — the
     * content of a reminder is rebuilt from the plan and the bundle every time.
     */
    private fun scheduledIds(): Set<String> = preferences().getStringSet(KEY_SCHEDULED, null).orEmpty()

    private fun rememberScheduledIds(ids: Set<String>) {
        preferences().edit().putStringSet(KEY_SCHEDULED, ids).apply()
    }

    private fun preferences() = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private companion object {
        const val PREFERENCES_NAME = "yadlo.reminders"
        const val KEY_SCHEDULED = "scheduled"
    }
}
