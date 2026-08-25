package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.about

import androidx.compose.runtime.Composable
import io.nicolaszurbuchen.yadlo.app.design.preview.YadloPreview
import io.nicolaszurbuchen.yadlo.infra.preview.PreviewThemes

/** One state, because the screen has one: nothing on it comes from the content. */
@PreviewThemes
@Composable
private fun AboutScreenPreview() {
    YadloPreview {
        AboutScreen(state = AboutUiModel(version = "1.0"), onBackClick = {}, onEmailClick = {})
    }
}
