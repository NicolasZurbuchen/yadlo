package io.nicolaszurbuchen.yadlo.infra.notification

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusProvisional
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual class Notifier {
    actual suspend fun isPermissionGranted(): Boolean =
        suspendCoroutine { continuation ->
            UNUserNotificationCenter.currentNotificationCenter().getNotificationSettingsWithCompletionHandler { settings ->
                val status = settings?.authorizationStatus
                continuation.resume(
                    status == UNAuthorizationStatusAuthorized || status == UNAuthorizationStatusProvisional,
                )
            }
        }

    actual suspend fun replaceScheduled(notifications: List<ScheduledNotification>) {
        val center = UNUserNotificationCenter.currentNotificationCenter()

        // One call clears everything this app holds, which is the whole reason the app replaces
        // rather than reconciles: iOS will not answer synchronously what is pending, and cancelling
        // the lot costs nothing.
        center.removeAllPendingNotificationRequests()

        val now = NSDate().timeIntervalSince1970

        notifications.forEach { notification ->
            // An interval trigger rather than a calendar one, because a calendar trigger fires at a
            // wall-clock time in whatever zone the phone is in when it arrives. Somebody driving to
            // Préverenges from another zone would get a reminder an hour out; this fires at the
            // instant, wherever they are.
            val seconds = (notification.at.toEpochMilliseconds() / MILLIS_PER_SECOND) - now
            if (seconds <= 0.0) return@forEach

            center.addNotificationRequest(
                UNNotificationRequest.requestWithIdentifier(
                    identifier = notification.id,
                    content = content(notification),
                    trigger =
                        UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
                            timeInterval = seconds,
                            repeats = false,
                        ),
                ),
                withCompletionHandler = null,
            )
        }
    }

    private fun content(notification: ScheduledNotification) =
        UNMutableNotificationContent().apply {
            setTitle(notification.title)
            setBody(notification.body)
            setSound(UNNotificationSound.defaultSound)

            // Threads four sets starting at 22:00 into one stack rather than four rows, the same
            // thing the group key does on Android.
            setThreadIdentifier(REMINDER_THREAD_ID)

            setUserInfo(
                buildMap {
                    put(USER_INFO_TARGET, notification.target.encode())
                    notification.staleAfter?.let {
                        put(USER_INFO_STALE_AFTER, it.toEpochMilliseconds() / MILLIS_PER_SECOND)
                    }
                },
            )
        }

    /**
     * The other half of "does a reminder for something already over stay in the shade?".
     *
     * Android is told a timeout when the alarm is scheduled and dismisses the notification itself.
     * iOS has no equivalent, so the only moment it can be swept is a moment the app is running —
     * which is this one. A notification that fired two minutes ago for a set starting in twenty-eight
     * is deliberately left alone: staleness is the Slot's *end*, not the reminder's own instant.
     *
     * Fire-and-forget rather than awaited. Nothing depends on the answer, and a shade tidied a
     * fraction of a second after the app opened is indistinguishable from one tidied before.
     */
    actual suspend fun clearStaleDelivered() {
        val center = UNUserNotificationCenter.currentNotificationCenter()

        center.getDeliveredNotificationsWithCompletionHandler { delivered ->
            val now = NSDate().timeIntervalSince1970

            val stale =
                delivered
                    .orEmpty()
                    .filterIsInstance<UNNotification>()
                    .filter { notification ->
                        val staleAfter = notification.request.content.userInfo[USER_INFO_STALE_AFTER] as? Double
                        staleAfter != null && staleAfter < now
                    }
                    .map { it.request.identifier }

            if (stale.isNotEmpty()) {
                center.removeDeliveredNotificationsWithIdentifiers(stale)
            }
        }
    }

    private companion object {
        const val MILLIS_PER_SECOND = 1000.0
        const val REMINDER_THREAD_ID = "yadlo.reminders"
    }
}

internal const val USER_INFO_TARGET = "yadlo.notification.target"
internal const val USER_INFO_STALE_AFTER = "yadlo.notification.staleAfter"
