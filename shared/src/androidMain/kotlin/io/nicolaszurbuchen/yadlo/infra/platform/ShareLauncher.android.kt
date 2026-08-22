package io.nicolaszurbuchen.yadlo.infra.platform

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberShareLauncher(): ShareLauncher {
    val context = LocalContext.current

    return remember(context) {
        ShareLauncher { text ->
            val intent =
                Intent(Intent.ACTION_SEND).apply {
                    type = MIME_PLAIN_TEXT
                    putExtra(Intent.EXTRA_TEXT, text)
                }

            // Wrapped in a chooser rather than fired bare. Android will otherwise offer to
            // remember a default app for ACTION_SEND, and a visitor who once picked one is then
            // unable to share to anything else without clearing that default in Settings.
            context.startActivity(Intent.createChooser(intent, null))
        }
    }
}

private const val MIME_PLAIN_TEXT = "text/plain"
