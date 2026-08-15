package io.nicolaszurbuchen.yadlo

import androidx.compose.ui.window.ComposeUIViewController
import io.nicolaszurbuchen.yadlo.app.App
import io.nicolaszurbuchen.yadlo.app.di.initKoin
import org.koin.mp.KoinPlatform
import platform.UIKit.UIViewController
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

@OptIn(ExperimentalNativeApi::class)
@Suppress("ktlint:standard:function-naming")
fun MainViewController(): UIViewController {
    if (KoinPlatform.getKoinOrNull() == null) {
        initKoin(isDebugBuild = Platform.isDebugBinary)
    }
    return ComposeUIViewController { App() }
}
