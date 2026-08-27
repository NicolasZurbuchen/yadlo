package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.privacy

import androidx.compose.runtime.Composable
import io.nicolaszurbuchen.yadlo.design.preview.YadloPreview
import io.nicolaszurbuchen.yadlo.infra.preview.PreviewThemes

/**
 * One state, because the screen has one: nothing on it comes from the content.
 *
 * The dark rendering is where the marks need looking at: three green ✓ in a column is the app's
 * largest single use of the [io.nicolaszurbuchen.yadlo.design.uimodel.YadloFactMarkUiModel]
 * tint, and a green chosen against a white page is exactly the sort that quietly disappears into a
 * dark one.
 */
@PreviewThemes
@Composable
private fun PrivacyScreenPreview() {
    YadloPreview {
        PrivacyScreen(onBackClick = {})
    }
}
