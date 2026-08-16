package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.about

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
private fun AboutScreenPreview() {
    YadloTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            AboutScreen(state = AboutUiModel(version = "1.0"), onBackClick = {}, onEmailClick = {})
        }
    }
}

@Preview
@Composable
private fun AboutScreenDarkPreview() {
    YadloTheme(darkTheme = true) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            AboutScreen(state = AboutUiModel(version = "1.0"), onBackClick = {}, onEmailClick = {})
        }
    }
}
