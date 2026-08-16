package io.nicolaszurbuchen.yadlo

import androidx.compose.ui.window.ComposeUIViewController
import io.nicolaszurbuchen.yadlo.app.App
import io.nicolaszurbuchen.yadlo.app.di.initKoin
import org.koin.mp.KoinPlatform
import platform.Foundation.NSBundle
import platform.UIKit.UIViewController
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

@OptIn(ExperimentalNativeApi::class)
@Suppress("ktlint:standard:function-naming")
fun MainViewController(): UIViewController {
    if (KoinPlatform.getKoinOrNull() == null) {
        initKoin(isDebugBuild = Platform.isDebugBinary, appVersion = marketingVersion())
    }
    return ComposeUIViewController { App() }
}

/**
 * `CFBundleShortVersionString`, which is the number the App Store shows and the one a visitor would
 * quote — not `CFBundleVersion`, which is the build counter and means nothing outside a CI log.
 *
 * The fallback is not defensive padding: the key is genuinely absent from a bundle assembled without
 * an Info.plist, which is what a bare `ComposeUIViewController` harness is, and *À propos* printing
 * an empty line there would look like a bug in the screen rather than in the bundle.
 */
private fun marketingVersion(): String = NSBundle.mainBundle.objectForInfoDictionaryKey(SHORT_VERSION_KEY) as? String ?: UNKNOWN_VERSION

private const val SHORT_VERSION_KEY = "CFBundleShortVersionString"
private const val UNKNOWN_VERSION = "—"
