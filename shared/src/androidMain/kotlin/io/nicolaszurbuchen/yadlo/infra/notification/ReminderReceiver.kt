package io.nicolaszurbuchen.yadlo.infra.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import io.nicolaszurbuchen.yadlo.shared.R

/**
 * Posts one reminder, at the moment its alarm goes off.
 *
 * **It reads only its own extras.** No Koin, no content bundle, no string resources, no database —
 * the prose was resolved when the alarm was scheduled and travelled with it. That is not tidiness:
 * an alarm fires into a process that may have just been started for this one broadcast, with ten
 * seconds to finish, and every dependency it did not need is a way for that to fail on somebody's
 * phone at 21:30 on the Saturday.
 */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val id = intent.getStringExtra(EXTRA_ID) ?: return
        val title = intent.getStringExtra(EXTRA_TITLE) ?: return
        val body = intent.getStringExtra(EXTRA_BODY) ?: return

        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return

        val builder =
            NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                // Tints the icon and the app name on the notification itself. The small icon in the
                // status bar is not affected — the system owns that one and paints it to match
                // whatever is around it.
                .setColor(ContextCompat.getColor(context, R.color.notification_accent))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setGroup(REMINDER_GROUP_KEY)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(launchIntent(context, id, intent.getStringExtra(EXTRA_TARGET)))

        // Half of the answer to "does a reminder for something already over stay in the shade?".
        // Android is told once, here, and dismisses it itself when the Slot ends. iOS has no
        // equivalent and sweeps on next launch instead.
        intent.takeIf { it.hasExtra(EXTRA_TIMEOUT_MILLIS) }
            ?.getLongExtra(EXTRA_TIMEOUT_MILLIS, 0L)
            ?.takeIf { it > 0 }
            ?.let(builder::setTimeoutAfter)

        manager.notify(id.hashCode(), builder.build())
    }

    /**
     * The app's own launcher intent rather than `MainActivity` by name: this class lives in the
     * shared module, which cannot see the Android application module and should not be made to.
     *
     * `SINGLE_TOP` is what makes a tap on a running app arrive at `onNewIntent` instead of building
     * a second copy of the shell on top of the one the visitor already had open.
     */
    private fun launchIntent(
        context: Context,
        id: String,
        target: String?,
    ): PendingIntent? {
        val launch =
            context.packageManager
                .getLaunchIntentForPackage(context.packageName)
                ?.apply {
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    putExtra(EXTRA_TARGET, target)
                }
                ?: return null

        return PendingIntent.getActivity(
            context,
            id.hashCode(),
            launch,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
