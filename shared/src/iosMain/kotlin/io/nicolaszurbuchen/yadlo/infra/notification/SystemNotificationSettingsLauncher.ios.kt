package io.nicolaszurbuchen.yadlo.infra.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

@Composable
actual fun rememberSystemNotificationSettingsLauncher(): SystemNotificationSettingsLauncher =
    remember {
        SystemNotificationSettingsLauncher {
            // iOS offers one destination — the app's own page in Settings — and no way to deep-link
            // to the notification section within it. One tap closer than nothing.
            NSURL.URLWithString(UIApplicationOpenSettingsURLString)?.let { url ->
                UIApplication.sharedApplication.openURL(url, options = emptyMap<Any?, Any>(), completionHandler = null)
            }
        }
    }
