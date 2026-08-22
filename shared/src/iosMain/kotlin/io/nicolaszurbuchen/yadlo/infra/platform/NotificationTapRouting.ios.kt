package io.nicolaszurbuchen.yadlo.infra.platform

import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationPresentationOptionBanner
import platform.UserNotifications.UNNotificationPresentationOptionSound
import platform.UserNotifications.UNNotificationPresentationOptions
import platform.UserNotifications.UNNotificationResponse
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject

/**
 * Starts listening for notification taps.
 *
 * Called from the composition root rather than lazily, because a tap on a notification is one of the
 * ways this app gets launched at all: the delegate has to be in place before the system delivers the
 * response, which happens moments after the process starts and long before any screen exists.
 */
fun startNotificationTapRouting(relay: NotificationTargetRelay) {
    val created = NotificationTapDelegate(relay)

    // UNUserNotificationCenter holds its delegate weakly, so without this the only strong reference
    // would be the local above and the delegate would be collected before the first tap arrives.
    tapDelegate = created

    UNUserNotificationCenter.currentNotificationCenter().delegate = created
}

private var tapDelegate: NotificationTapDelegate? = null

private class NotificationTapDelegate(
    private val relay: NotificationTargetRelay,
) : NSObject(),
    UNUserNotificationCenterDelegateProtocol {
    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        didReceiveNotificationResponse: UNNotificationResponse,
        withCompletionHandler: () -> Unit,
    ) {
        (didReceiveNotificationResponse.notification.request.content.userInfo[USER_INFO_TARGET] as? String)
            ?.let(NotificationTarget::decode)
            ?.let(relay::post)

        withCompletionHandler()
    }

    /**
     * Without this iOS suppresses a notification that fires while the app is open. During the
     * festival that is most of them — somebody looking at the Programme is exactly who a reminder
     * about the next set is for.
     */
    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        willPresentNotification: UNNotification,
        withCompletionHandler: (UNNotificationPresentationOptions) -> Unit,
    ) {
        withCompletionHandler(UNNotificationPresentationOptionBanner or UNNotificationPresentationOptionSound)
    }
}
