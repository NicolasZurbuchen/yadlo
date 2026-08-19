package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.privacy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.nicolaszurbuchen.yadlo.app.design.theme.YadloTheme
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors

/** One state, because the screen has one: nothing on it comes from the content. */
@Preview
@Composable
private fun PrivacyScreenPreview() {
    YadloTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            PrivacyScreen(onBackClick = {})
        }
    }
}

/**
 * The dark half, and on this screen it is the marks that need looking at: three green ✓ in a column
 * is the app's largest single use of the new [io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloFactMarkUiModel]
 * tint, and a green chosen against a white page is exactly the sort that quietly disappears into a
 * dark one.
 */
@Preview
@Composable
private fun PrivacyScreenDarkPreview() {
    YadloTheme(darkTheme = true) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            PrivacyScreen(onBackClick = {})
        }
    }
}
