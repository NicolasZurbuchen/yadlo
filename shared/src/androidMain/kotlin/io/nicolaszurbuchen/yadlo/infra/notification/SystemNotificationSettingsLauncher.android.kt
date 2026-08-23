package io.nicolaszurbuchen.yadlo.infra.notification

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberSystemNotificationSettingsLauncher(): SystemNotificationSettingsLauncher {
    val context = LocalContext.current

    return remember(context) {
        SystemNotificationSettingsLauncher {
            val intent =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Lands directly on Yadlo's notification categories, which is one screen closer
                    // than the app's general settings page.
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                } else {
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.parse("package:${context.packageName}"))
                }

            // Started from a composable rather than an Activity context in every case, so the task
            // flag is not optional — without it this throws when the caller is an application
            // context rather than merely landing in the wrong place.
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
}
