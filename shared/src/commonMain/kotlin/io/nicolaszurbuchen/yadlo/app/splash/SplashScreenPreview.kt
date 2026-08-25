package io.nicolaszurbuchen.yadlo.app.splash

import androidx.compose.runtime.Composable
import io.nicolaszurbuchen.yadlo.app.design.preview.YadloPreview
import io.nicolaszurbuchen.yadlo.infra.preview.PreviewThemes

/**
 * No PreviewParameterProvider here: the splash takes no UiModel and has exactly one state. A
 * provider would be a sequence of one, which is scaffolding pretending to be coverage. Screens that
 * do carry a UiModel get one.
 */
@PreviewThemes
@Composable
private fun SplashScreenPreview() {
    YadloPreview {
        SplashScreen(onFinish = {})
    }
}
