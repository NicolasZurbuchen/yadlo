package io.nicolaszurbuchen.yadlo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.nicolaszurbuchen.yadlo.app.App
import io.nicolaszurbuchen.yadlo.infra.platform.NotificationTargetRelay
import io.nicolaszurbuchen.yadlo.infra.platform.notificationTarget
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val notificationRelay: NotificationTargetRelay by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Read before the content is set, so the shell finds the target already waiting on its first
        // composition rather than navigating a frame after it has drawn.
        routeNotificationTap(intent)

        setContent {
            App()
        }
    }

    /**
     * The warm path. A notification tap on an app that is already running arrives here rather than
     * at [onCreate], which is what the launch intent's `SINGLE_TOP` flag is for.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        routeNotificationTap(intent)
    }

    private fun routeNotificationTap(intent: Intent?) {
        intent?.notificationTarget()?.let(notificationRelay::post)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
