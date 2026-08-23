package io.nicolaszurbuchen.yadlo.infra.notification

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat

@Composable
internal actual fun rememberPlatformNotificationPermissionRequester(): NotificationPermissionRequester {
    val context = LocalContext.current

    // Held outside the contract callback because the launcher gives the result to a lambda it was
    // built with, not to the caller of launch(). One requester, one pending caller — which is true
    // here, since the only call site asks once per session.
    val pending = remember { mutableListOf<(Boolean) -> Unit>() }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            pending.forEach { it(granted) }
            pending.clear()
        }

    return remember(context, launcher) {
        NotificationPermissionRequester { onResult ->
            // POST_NOTIFICATIONS did not exist before Android 13, where notifications are permitted
            // until the visitor turns them off in settings. Asking there would launch a contract for
            // a permission the system does not know, so the honest answer is whatever the channel
            // state already says.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                onResult(NotificationManagerCompat.from(context).areNotificationsEnabled())
                return@NotificationPermissionRequester
            }

            pending += onResult
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
