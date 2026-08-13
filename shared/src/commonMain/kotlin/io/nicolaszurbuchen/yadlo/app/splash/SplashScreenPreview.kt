package io.nicolaszurbuchen.yadlo.app.splash

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.nicolaszurbuchen.yadlo.app.design.theme.YadloTheme

/**
 * No PreviewParameterProvider here: the splash takes no UiModel and has exactly one state. A
 * provider would be a sequence of one, which is scaffolding pretending to be coverage. Screens that
 * do carry a UiModel get one.
 *
 * Dark is driven through [YadloTheme] rather than `uiMode`, because `UI_MODE_NIGHT_YES` is an
 * Android constant and this is commonMain.
 */
@Preview
@Composable
private fun SplashScreenLightPreview() {
    YadloTheme(darkTheme = false) {
        SplashScreen(onFinish = {})
    }
}

@Preview
@Composable
private fun SplashScreenDarkPreview() {
    YadloTheme(darkTheme = true) {
        SplashScreen(onFinish = {})
    }
}
