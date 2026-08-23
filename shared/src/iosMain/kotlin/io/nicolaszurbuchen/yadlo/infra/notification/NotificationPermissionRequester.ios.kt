package io.nicolaszurbuchen.yadlo.infra.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
internal actual fun rememberPlatformNotificationPermissionRequester(): NotificationPermissionRequester =
    remember {
        NotificationPermissionRequester { onResult ->
            UNUserNotificationCenter.currentNotificationCenter().requestAuthorizationWithOptions(
                UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge,
            ) { granted, _ ->
                // The completion handler arrives on an arbitrary queue. Whatever the caller does with
                // this answer ends up in Compose state, so it is put back on the main queue first.
                dispatch_async(dispatch_get_main_queue()) { onResult(granted) }
            }
        }
    }
