package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.app.design.theme.YadloTheme
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors

/**
 * The real 2026 hours, and the skeleton they arrive into. Saturday is the one worth looking at: it
 * closes at 03:00 the next morning, which is a window crossing midnight and would read as a mistake
 * if the card did anything other than print it.
 */
private class HoursStateProvider : PreviewParameterProvider<HoursUiModel> {
    override val values =
        sequenceOf(
            HoursUiModel(isLoading = true, days = emptyList(), caveat = null, emptyMessage = null),
            published(),
        )
}

@Preview
@Composable
private fun HoursScreenPreview(
    @PreviewParameter(HoursStateProvider::class) state: HoursUiModel,
) {
    YadloTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            HoursScreen(state = state, onBackClick = {})
        }
    }
}

@Preview
@Composable
private fun HoursScreenDarkPreview(
    @PreviewParameter(HoursStateProvider::class) state: HoursUiModel,
) {
    YadloTheme(darkTheme = true) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            HoursScreen(state = state, onBackClick = {})
        }
    }
}

private fun published() =
    HoursUiModel(
        isLoading = false,
        days =
            listOf(
                OpeningDayUiModel(id = "2026:fri", name = "Vendredi", window = "16:00 – 02:00"),
                OpeningDayUiModel(id = "2026:sat", name = "Samedi", window = "12:00 – 03:00"),
                OpeningDayUiModel(id = "2026:sun", name = "Dimanche", window = "12:00 – 22:00"),
            ),
        caveat = null,
        emptyMessage = null,
    )
