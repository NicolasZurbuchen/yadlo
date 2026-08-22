@file:OptIn(ExperimentalForeignApi::class)

package io.nicolaszurbuchen.yadlo.infra.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import platform.UIKit.popoverPresentationController

@Composable
actual fun rememberShareLauncher(): ShareLauncher =
    remember {
        ShareLauncher { text ->
            val presenter = topViewController() ?: return@ShareLauncher

            val controller =
                UIActivityViewController(
                    activityItems = listOf(text),
                    applicationActivities = null,
                )

            // **The popover anchor is why this is not two lines.** On an iPhone the sheet slides up
            // from the bottom and needs nothing. On an iPad UIKit presents it as a popover and
            // raises rather than guessing where to point it — and "iPad, iPhone compatibility mode"
            // is a shipping configuration rather than a hypothetical. Anchored to the middle of the
            // presenting view, which is where a share with no button to point at belongs anyway.
            controller.popoverPresentationController?.sourceView = presenter.view

            presenter.presentViewController(controller, animated = true, completion = null)
        }
    }

/**
 * The view controller currently on screen, walking past anything already presented over the root.
 *
 * `UIApplication.keyWindow` has been deprecated since iOS 13 because an app may own several scenes;
 * this reads the foreground scene's key window instead, which is the same answer for a single-scene
 * app and the correct one for any other.
 */
private fun topViewController(): UIViewController? {
    val root =
        UIApplication.sharedApplication.connectedScenes
            .filterIsInstance<UIWindowScene>()
            .firstNotNullOfOrNull { scene ->
                scene.windows
                    .filterIsInstance<UIWindow>()
                    .firstOrNull { it.isKeyWindow() }
                    ?.rootViewController
            }

    var top = root
    while (top?.presentedViewController != null) {
        top = top.presentedViewController
    }

    return top
}
