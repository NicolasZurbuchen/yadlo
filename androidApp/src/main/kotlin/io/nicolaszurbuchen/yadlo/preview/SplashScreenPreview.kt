package io.nicolaszurbuchen.yadlo.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.nicolaszurbuchen.yadlo.app.design.theme.YadloTheme
import io.nicolaszurbuchen.yadlo.app.splash.SplashScreen

/**
 * Previews live here rather than beside the composable they render.
 *
 * `shared` is built with AGP's KMP library plugin, which produces no debug variant, and Android
 * Studio's Compose preview needs one — a `@Preview` in `commonMain` compiles but does not render.
 * `androidApp` is an ordinary Android application module with `debugImplementation(uiTooling)`,
 * which is what the renderer actually requires.
 *
 * The cost is that a preview sits in a different module from its screen. The alternative was a
 * preview that looks present and silently never draws, which is worse: it is the kind of thing
 * someone trusts until the one time it matters.
 */
@Preview(name = "Splash", showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
private fun SplashScreenPreview() {
    // Wrapped in YadloTheme because the screen reads the scrim and its ink from it. Without the
    // theme it would render against the composition-local defaults and stop telling the truth about
    // the one thing worth previewing here — whether the logos are legible on the photograph.
    YadloTheme {
        SplashScreen(onFinish = {})
    }
}

/**
 * The splash is deliberately theme-independent: the scrim and its ink sit on a photograph rather
 * than on one of the app's grounds. This preview exists to make that visible rather than assumed —
 * the two should be identical.
 */
@Preview(name = "Splash · dark theme", showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
private fun SplashScreenDarkThemePreview() {
    YadloTheme(darkTheme = true) {
        SplashScreen(onFinish = {})
    }
}
