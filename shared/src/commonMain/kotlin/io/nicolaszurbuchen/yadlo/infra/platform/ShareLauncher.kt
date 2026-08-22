package io.nicolaszurbuchen.yadlo.infra.platform

import androidx.compose.runtime.Composable

/**
 * Hands a piece of text to whatever the platform uses for sharing — the Android chooser, the iOS
 * activity sheet.
 *
 * **Text and nothing else, on purpose.** Neither platform is asked for a title, a subject or a
 * mime type beyond plain text, because everything downstream of this treats a share as a message
 * somebody is about to send: the recipient reads it in WhatsApp or a text, not in a preview card.
 * A richer payload would also be the first step towards a share sheet that behaves differently on
 * the two platforms, which is the thing an `expect`/`actual` seam exists to prevent.
 *
 * A `@Composable` factory rather than an injected service because both platforms need something
 * only the composition has: Android a `Context` to start an activity from, iOS a view controller to
 * present the sheet on. Passing either through Koin would mean holding a reference to a screen in a
 * singleton, which is how an Activity leaks.
 *
 * @see io.nicolaszurbuchen.yadlo.common.share.ShareText for what the app actually puts in it.
 */
fun interface ShareLauncher {
    fun share(text: String)
}

@Composable
expect fun rememberShareLauncher(): ShareLauncher
